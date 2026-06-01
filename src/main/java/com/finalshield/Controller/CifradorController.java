package com.finalshield.Controller;

import com.finalshield.Auditoria.AuditoriaEventoTipo;
import com.finalshield.DTO.Cifrado.DescifradoRequest;
import com.finalshield.Model.Archivo;
import com.finalshield.Model.Usuario;
import com.finalshield.Repositorios.ArchivoRepositorio;
import com.finalshield.Services.AuditoriaEventoService;
import com.finalshield.Services.CifradorAESService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/cifrado")
public class CifradorController {

    @Autowired
    private AuditoriaEventoService auditoriaService;

    @Autowired
    private CifradorAESService cifradorAESService;

    @Autowired
    private ArchivoRepositorio archivoRepositorio;

    /*Endpoint para descifrar texto (usado en otras partes de la app, como pruebas o correo).
     Recibe texto cifrado en base64 y la clave en base64, devuelve el texto plano.*/
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
    /*Endpoint principal para cifrar archivos subidos desde el móvil.
     Recibe uno o más archivos multipart, los cifra usando la clave única del usuario autenticado,
     los guarda en el servidor y registra los metadatos en la base de datos*/
    @PostMapping("/cifrar")
    public ResponseEntity<List<Archivo>> cifrarArchivos(@RequestParam("archivo") List<MultipartFile> files, Authentication authentication) {
        //validar autenticación
        if (authentication == null || !(authentication.getPrincipal() instanceof Usuario)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Usuario user = (Usuario) authentication.getPrincipal();

        // la clave unica del usuario
        String claveBase64Usuario = user.getClaveCifDesPersonal();
        if (claveBase64Usuario == null || claveBase64Usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // El usuario no tiene clave configurada
        }
        SecretKey claveUsuario = cifradorAESService.base64AClave(claveBase64Usuario);

        List<Archivo> saved = new ArrayList<>();

        try {
            for (MultipartFile file : files) {
                //guardar el archivo original temporalmente
                File tempOriginal = File.createTempFile("original-", file.getOriginalFilename());
                file.transferTo(tempOriginal);

                //definir ruta segura en el servidor (ajusta la carpeta base según tu entorno)
                String storagePath = "/var/app/files/" + user.getIdUsuario() + "/" + UUID.randomUUID().toString() + ".enc";
                File encrypted = new File(storagePath);
                encrypted.getParentFile().mkdirs(); // Crear directorios si no existen

                //cifrar el archivo usando la clave única del usuario
                cifradorAESService.cifrarArchivo(tempOriginal, encrypted, claveUsuario);

                //crear entidad Archivo con metadatos
                Archivo arch = new Archivo();
                arch.setNombreArchivo(file.getOriginalFilename());
                arch.setEstado("Cifrado");
                arch.setTipoArchivo(file.getContentType());
                arch.setRutaArchivo(storagePath);
                arch.setTamano(encrypted.length());
                arch.setFechaSubida(LocalDateTime.now());
                arch.setUsuario(user);
                arch.setCarpetaMonitorizada(null); // Ajustar si es necesario

                //guardar en base de datos
                saved.add(archivoRepositorio.save(arch));

                auditoriaService.registrarEvento(
                        user.getIdUsuario(),
                        AuditoriaEventoTipo.FILE_ENCRYPTED,
                        "Archivo cifrado: " + arch.getNombreArchivo(),
                        true
                );

                //eliminar archivo temporal
                tempOriginal.delete();
            }
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            auditoriaService.registrarEvento(
                    user.getIdUsuario(),
                    AuditoriaEventoTipo.FILE_ENCRYPTED,
                    "Error al cifrar archivo: " + e.getMessage(),
                    false
            );
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /*Endpoint para descargar un archivo cifrado.
     Verifica que el archivo pertenezca al usuario autenticado y lo envía como stream*/
    @GetMapping("/descargarCifrado/{idArchivo}")
    public ResponseEntity<Resource> descargarCifrado(@PathVariable Integer idArchivo, Authentication authentication) throws FileNotFoundException {
        if (authentication == null || !(authentication.getPrincipal() instanceof Usuario)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Usuario user = (Usuario) authentication.getPrincipal();

        Optional<Archivo> optionalArch = archivoRepositorio.findByIdArchivoAndUsuario(idArchivo, user);
        if (optionalArch.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Archivo arch = optionalArch.get();
        File file = new File(arch.getRutaArchivo());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(arch.getTamano())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + arch.getNombreArchivo() + ".enc\"")
                .body(resource);
    }

    /*Endpoint para listar todos los archivos cifrados del usuario autenticado,
     usado por el frontend al cargar el fragmento.*/
    @GetMapping("/list")
    public ResponseEntity<List<Archivo>> getAllArchivos(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Usuario)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Usuario user = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(archivoRepositorio.findByUsuario(user));
    }

    /*Endpoint para borrar un archivo cifrado.
    Elimina el archivo físico del servidor y la entrada en la base de datos,
    solo permite borrar archivos del usuario autenticado.*/
    @DeleteMapping("/borrar/{idArchivo}")
    public ResponseEntity<Void> borrarArchivo(@PathVariable Integer idArchivo, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Usuario)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Usuario user = (Usuario) authentication.getPrincipal();

        Optional<Archivo> optionalArch = archivoRepositorio.findByIdArchivoAndUsuario(idArchivo, user);
        if (optionalArch.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Archivo arch = optionalArch.get();

        File file = new File(arch.getRutaArchivo());
        if (file.exists()) {
            file.delete(); //borrar archivo físico
        }
        archivoRepositorio.delete(arch); //borrar registro en DB
        return ResponseEntity.ok().build();
    }
    @GetMapping("/descifrarArchivo/{idArchivo}")
    public ResponseEntity<Resource> descifrarArchivo(
            @PathVariable Integer idArchivo,
            Authentication authentication) {

        if (authentication == null ||
                !(authentication.getPrincipal() instanceof Usuario)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Usuario user = (Usuario) authentication.getPrincipal();

        Optional<Archivo> optionalArch =
                archivoRepositorio.findByIdArchivoAndUsuario(idArchivo, user);

        if (optionalArch.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Archivo arch = optionalArch.get();
        File fileCifrado = new File(arch.getRutaArchivo());

        try {

            File tempDescifrado =
                    File.createTempFile("desc-", arch.getNombreArchivo());

            SecretKey claveUsuario =
                    cifradorAESService.base64AClave(user.getClaveCifDesPersonal());

            cifradorAESService.descifrarArchivo(
                    fileCifrado,
                    tempDescifrado,
                    claveUsuario
            );

            arch.setEstado("Descifrado");
            archivoRepositorio.save(arch);

            // AUDITORÍA
            auditoriaService.registrarEvento(
                    user.getIdUsuario(),
                    AuditoriaEventoTipo.FILE_DECRYPTED,
                    "Archivo descifrado: " + arch.getNombreArchivo(),
                    true
            );

            InputStreamResource resource =
                    new InputStreamResource(new FileInputStream(tempDescifrado));

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(arch.getTipoArchivo()))
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + arch.getNombreArchivo() + "\""
                    )
                    .body(resource);

        } catch (Exception e) {

            // AUDITORÍA DE ERROR
            auditoriaService.registrarEvento(
                    user.getIdUsuario(),
                    AuditoriaEventoTipo.FILE_DECRYPTED,
                    "Error al descifrar archivo: " + e.getMessage(),
                    false
            );

            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /*
    @PostMapping("/descifrar")
    public ResponseEntity<?> descifrarTexto(@RequestBody DescifradoRequest request) {
        try {
            // Convierte la clave Base64 a SecretKey
            SecretKey clave = cifradorAESService.base64AClave(request.getClaveBase64());
            // Descifra el texto usando el AES
            String textoDescifrado = cifradorAESService.descifrarTexto(
                    request.getTextoCifradoBase64(),
                    clave
            );
            // Devuelve el texto
            return ResponseEntity.ok(textoDescifrado);

        } catch (GeneralSecurityException e) {
            // Casos de errores
            System.err.println("Error de seguridad al descifrar texto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error de seguridad al descifrar el mensaje: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error interno del servidor al descifrar texto: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error interno al intentar descifrar el mensaje.");
        }
    }

    @PostMapping(value = "/cifrar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> cifrarArchivo(@RequestPart("archivo") MultipartFile archivo, @RequestPart("claveBase64") String claveBase64) {
        File temp = null;

        try {
            // Creación y guardado del archivo temporalmente para poder cifrarlo
            temp = File.createTempFile("entrada", archivo.getOriginalFilename());
            archivo.transferTo(temp);

            // Convierte la clave Base64 a SecretKey
            SecretKey clave = cifradorAESService.base64AClave(claveBase64);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            cifradorAESService.cifrarArchivoStream(temp, outputStream, clave);

            // Devuelve la clave cifrada en Base64
            return ResponseEntity.ok(
                    Base64.getEncoder().encodeToString(outputStream.toByteArray())
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al cifrar archivo: " + e.getMessage());

        } finally {
            // Borrado del archivo temporal
            if (temp != null && temp.exists()) temp.delete();
        }
    }
    */
}
