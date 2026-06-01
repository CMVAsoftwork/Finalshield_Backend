package com.finalshield.Services.Impl;

import com.finalshield.Auditoria.AuditoriaEventoTipo;
import com.finalshield.DTO.Carpeta.ArchivoDTO;
import com.finalshield.Model.Archivo;
import com.finalshield.Model.CarpetaMonitorizada;
import com.finalshield.Model.Usuario;
import com.finalshield.Repositorios.ArchivoRepositorio;
import com.finalshield.Repositorios.CarpetaMonitorizadaRepositorio;
import com.finalshield.Repositorios.UsuarioRepositorio;
import com.finalshield.Services.AuditoriaEventoService;
import com.finalshield.Services.CifradorAESService;
import com.finalshield.Services.EscanerService;
import com.finalshield.Services.UsuarioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.SecretKey;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EscanerServiceImpl implements EscanerService {
    @Autowired
    private AuditoriaEventoService auditoriaService;

    // Ruta base configurada en application.properties
    @Value("${finalshield.storage.base-path}")
    private String basePath;

    @Autowired
    private UsuarioRepositorio usuarioRepo;

    @Autowired
    private ArchivoRepositorio archivoRepo;

    @Autowired
    private CifradorAESService cifradorAESService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CarpetaMonitorizadaRepositorio carpetaRepo;

    @Override
    public List<ArchivoDTO> procesarArchivos(
            Integer idUsuario,
            String contrasena,
            List<MultipartFile> archivos,
            Integer idCarpeta
    ) throws Exception {
        return procesarArchivosInterno(idUsuario, contrasena, archivos, idCarpeta);
    }

    private List<ArchivoDTO> procesarArchivosInterno(
            Integer idUsuario,
            String contrasena,
            List<MultipartFile> archivos,
            Integer idCarpeta
    ) throws Exception {

        // Validar usuario y recuperar
        Usuario usuario = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        SecretKey claveAES = usuarioService.recuperarClaveAES(idUsuario, contrasena);

        Path carpetaUsuario = Paths.get(basePath, "usuario_" + idUsuario);
        Files.createDirectories(carpetaUsuario);

        Path carpetaDestino = carpetaUsuario;

        // Crear carpeta del usuario
        CarpetaMonitorizada carpeta = null;

        // Si se elige una carpeta
        if (idCarpeta != null) {
            carpeta = carpetaRepo.findById(idCarpeta)
                    .orElseThrow(() -> new RuntimeException("Carpeta no encontrada"));

            carpetaDestino = carpetaUsuario.resolve("carpeta_" + idCarpeta);
            Files.createDirectories(carpetaDestino);
        }

        List<ArchivoDTO> resultado = new ArrayList<>();

        // Procesar cada archivo subido
        for (MultipartFile archivoSubido : archivos) {

            String nombreOriginal = archivoSubido.getOriginalFilename();
            if (nombreOriginal == null)
                nombreOriginal = "archivo";

            // Guardar archivo original en disco
            Path archivoOriginal = carpetaDestino.resolve(nombreOriginal);
            archivoSubido.transferTo(archivoOriginal.toFile());

            // Cifrar archivo original
            Path archivoCifrado = carpetaDestino.resolve(nombreOriginal + ".enc");

            cifradorAESService.cifrarArchivo(
                    archivoOriginal.toFile(),
                    archivoCifrado.toFile(),
                    claveAES
            );

            // Eliminar archivo original de disco sin cifrar
            Files.deleteIfExists(archivoOriginal);

            // Guardar entidad de archivo en la BD
            Archivo entidad = new Archivo();
            entidad.setNombreArchivo(nombreOriginal + ".enc");
            entidad.setRutaArchivo(archivoCifrado.toAbsolutePath().toString());
            entidad.setEstado("CIFRADO");
            entidad.setTipoArchivo(archivoSubido.getContentType());
            entidad.setTamano(Files.size(archivoCifrado));
            entidad.setFechaSubida(LocalDateTime.now());
            entidad.setUsuario(usuario);

            if (carpeta != null) {
                entidad.setCarpetaMonitorizada(carpeta);
            }

            archivoRepo.save(entidad);
            auditoriaService.registrarEvento(
                    usuario.getIdUsuario(),
                    AuditoriaEventoTipo.FILE_ENCRYPTED,
                    "Archivo cifrado: " + nombreOriginal,
                    true
            );

            resultado.add(new ArchivoDTO(entidad));
        }

        return resultado;
    }
}
