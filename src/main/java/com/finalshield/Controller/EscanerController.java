package com.finalshield.Controller;

import com.finalshield.DTO.Carpeta.ArchivoDTO;
import com.finalshield.Services.EscanerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/escaner")
public class EscanerController {
    @Autowired
    private EscanerService escanerService;

    @PostMapping(value = "/cifrar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<ArchivoDTO>> escanearYCifrar(
            @RequestParam("idUsuario") Integer idUsuario,
            @RequestParam("contrasena") String contrasena,
            @RequestParam(value = "idCarpeta", required = false) Integer idCarpeta,
            @RequestPart("archivos") List<MultipartFile> archivos
    ) {
        try {
            // Procesa los archivos y devuelve la lista de archivos procesados
            List<ArchivoDTO> resultado =
                    escanerService.procesarArchivos(idUsuario, contrasena, archivos, idCarpeta);

            // Devuelve la lista de archivos procesados
            return ResponseEntity.ok(resultado);

        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
