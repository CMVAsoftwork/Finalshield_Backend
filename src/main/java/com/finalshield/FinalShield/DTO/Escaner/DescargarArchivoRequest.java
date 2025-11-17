package com.finalshield.FinalShield.DTO.Escaner;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DescargarArchivoRequest {
    private Integer idArchivo;
    private String clavePersonal;
}
