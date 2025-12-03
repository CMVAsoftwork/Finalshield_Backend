package com.finalshield.FinalShield.Services.Impl;

import com.finalshield.FinalShield.DTO.Carpeta.ArchivoDTO;
import com.finalshield.FinalShield.Model.Archivo;
import com.finalshield.FinalShield.Model.CarpetaMonitorizada;
import com.finalshield.FinalShield.Model.Usuario;
import com.finalshield.FinalShield.Repositorios.ArchivoRepositorio;
import com.finalshield.FinalShield.Repositorios.CarpetaMonitorizadaRepositorio;
import com.finalshield.FinalShield.Repositorios.UsuarioRepositorio;
import com.finalshield.FinalShield.Services.CifradorAESService;
import com.finalshield.FinalShield.Services.EscanerService;
import com.finalshield.FinalShield.Services.UsuarioService;
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

        Usuario usuario = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        SecretKey claveAES = usuarioService.recuperarClaveAES(idUsuario, contrasena);

        Path carpetaUsuario = Paths.get(basePath, "usuario_" + idUsuario);
        Files.createDirectories(carpetaUsuario);

        Path carpetaDestino = carpetaUsuario;

        CarpetaMonitorizada carpeta = null;

        if (idCarpeta != null) {
            carpeta = carpetaRepo.findById(idCarpeta)
                    .orElseThrow(() -> new RuntimeException("CarpetaMonitorizada no encontrada"));

            carpetaDestino = carpetaUsuario.resolve("carpeta_" + idCarpeta);
            Files.createDirectories(carpetaDestino);
        }

        List<ArchivoDTO> resultado = new ArrayList<>();

        for (MultipartFile archivoSubido : archivos) {

            String nombreOriginal = archivoSubido.getOriginalFilename();
            if (nombreOriginal == null)
                nombreOriginal = "archivo";

            Path archivoOriginal = carpetaDestino.resolve(nombreOriginal);
            archivoSubido.transferTo(archivoOriginal.toFile());

            Path archivoCifrado = carpetaDestino.resolve(nombreOriginal + ".enc");

            cifradorAESService.cifrarArchivo(
                    archivoOriginal.toFile(),
                    archivoCifrado.toFile(),
                    claveAES
            );

            Files.deleteIfExists(archivoOriginal);

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

            resultado.add(new ArchivoDTO(entidad));
        }

        return resultado;
    }
}
