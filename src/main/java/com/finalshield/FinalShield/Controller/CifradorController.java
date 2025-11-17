package com.finalshield.FinalShield.Controller;

import com.finalshield.FinalShield.DTO.Cifrado.DescifradoRequest;
import com.finalshield.FinalShield.Model.Archivo;
import com.finalshield.FinalShield.Model.Usuario;
import com.finalshield.FinalShield.Repositorios.ArchivoRepositorio;
import com.finalshield.FinalShield.Repositorios.UsuarioRepositorio;
import com.finalshield.FinalShield.Services.CifradorAESService;
import io.jsonwebtoken.io.IOException;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.SecretKey;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cifrado")
public class CifradorController {

    @Autowired
    private CifradorAESService cifradorAESService;

    @PostMapping("/descifrar")
    public ResponseEntity<?> descifrarTexto(@RequestBody DescifradoRequest request) {
        try {
            SecretKey clave = cifradorAESService.base64AClave(request.getClaveBase64());
            String textoDescifrado = cifradorAESService.descifrarTexto(
                    request.getTextoCifradoBase64(),
                    clave
            );
            return ResponseEntity.ok(textoDescifrado);

        } catch (GeneralSecurityException e) {
            System.err.println("Error de seguridad al descifrar texto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error de seguridad al descifrar el mensaje: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error interno del servidor al descifrar texto: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error interno al intentar descifrar el mensaje.");
        }
    }
    @Value("${app.storage.root-dir}")
    private String uploadDir;

    @Autowired
    private ArchivoRepositorio archivoRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    private final SecretKey CLAVE_SECRETA = cifradorAESService.generarClave();

    @PostMapping("/cifrar")
    public ResponseEntity<List<Archivo>> cifrarYGuardarArchivos(
            @RequestParam("files") List<MultipartFile> files,
            Authentication authentication) {

        if (files.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        List<Archivo> guardados = new ArrayList<>();
        String correo = authentication.getName(); // Assuming username is correo
        Optional<Usuario> usuarioOpt = usuarioRepositorio.findByCorreo(correo);
        if (usuarioOpt.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        Usuario usuarioActual = usuarioOpt.get();

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            try {
                String nombreOriginal = file.getOriginalFilename();
                String nombreCifrado = nombreOriginal + "_" + System.currentTimeMillis() + ".enc";

                Path rutaCompletaCifrado = Paths.get(uploadDir, nombreCifrado);
                Files.createDirectories(rutaCompletaCifrado.getParent());
                File archivoDestino = rutaCompletaCifrado.toFile();

                Path archivoTemporal = Files.createTempFile("upload-temp", null);
                file.transferTo(archivoTemporal);

                cifradorAESService.cifrarArchivo(archivoTemporal.toFile(), archivoDestino, CLAVE_SECRETA);

                Files.deleteIfExists(archivoTemporal);

                Archivo archivo = new Archivo();
                archivo.setUsuario(usuarioActual);

                archivo.setNombreArchivo(nombreOriginal);
                archivo.setTipoArchivo(file.getContentType());
                archivo.setTamano(file.getSize());
                archivo.setEstado("Cifrado");
                archivo.setFechaSubida(LocalDateTime.now());
                archivo.setRutaArchivo(rutaCompletaCifrado.toString());

                Archivo archivoGuardado = archivoRepositorio.save(archivo);
                guardados.add(archivoGuardado);

            } catch (IOException | GeneralSecurityException e) {
                System.err.println("Error en el proceso de cifrado: " + e.getMessage());
                e.printStackTrace();
                // Continúa con otros archivos, o retorna error si prefieres
            } catch (java.io.IOException e) {
                throw new RuntimeException(e);
            }
        }

        return new ResponseEntity<>(guardados, HttpStatus.CREATED);
    }

    @GetMapping("/descargarCifrado/{idArchivo}")
    public ResponseEntity<Resource> descargarCifrado(@PathVariable Integer idArchivo) {
        try {
            byte[] archivoCifradoBytes = cifradorAESService.obtenerContenidoCifrado(idArchivo);

            if (archivoCifradoBytes == null) {
                return ResponseEntity.notFound().build();
            }

            String nombreDescarga = "archivo_cifrado_" + idArchivo + ".enc";

            ByteArrayResource resource = new ByteArrayResource(archivoCifradoBytes);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreDescarga + "\"")
                    .contentLength(archivoCifradoBytes.length)
                    .body((Resource) resource);

        } catch (Exception e) {
            System.err.println("Error interno del servidor al descargar contenido cifrado: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/list")
    public List<Archivo> getAllArchivos(Authentication authentication) {
        String correo = authentication.getName();
        Optional<Usuario> usuarioOpt = usuarioRepositorio.findByCorreo(correo);
        if (usuarioOpt.isEmpty()) {
            return new ArrayList<>();
        }
        return archivoRepositorio.findByUsuario_IdUsuario(usuarioOpt.get().getIdUsuario());
    }
    @DeleteMapping("/borrar/{idArchivo}")
    public ResponseEntity<Void> borrarArchivo(@PathVariable Integer idArchivo, Authentication authentication) {
        String correo = authentication.getName();
        Optional<Usuario> usuarioOpt = usuarioRepositorio.findByCorreo(correo);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<Archivo> archivoOpt = archivoRepositorio.findById(idArchivo);
        if (archivoOpt.isEmpty() || !archivoOpt.get().getUsuario().getIdUsuario().equals(usuarioOpt.get().getIdUsuario())) {
            return ResponseEntity.notFound().build();
        }

        Archivo archivo = archivoOpt.get();
        Path ruta = Paths.get(archivo.getRutaArchivo());
        try {
            Files.deleteIfExists(ruta); // Borra físico
            archivoRepositorio.delete(archivo); // Borra en MySQL
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            System.err.println("Error al borrar archivo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }
}
