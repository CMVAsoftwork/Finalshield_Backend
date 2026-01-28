package com.finalshield.Services;

import com.finalshield.DTO.Carpeta.ArchivoDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EscanerService {
    List<ArchivoDTO> procesarArchivos(
            Integer idUsuario,
            String contrasena,
            List<MultipartFile> archivos,
            Integer idCarpeta
    ) throws Exception;

}
