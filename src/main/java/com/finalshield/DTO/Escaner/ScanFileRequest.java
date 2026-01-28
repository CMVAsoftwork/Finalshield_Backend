package com.finalshield.DTO.Escaner;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ScanFileRequest {
    private String nombreArchivo;
    private String archivoBase64;
    private String hashSha256;
    private String claveCifradoBase64;
}
