package com.finalshield.FinalShield.DTO.Cifrado;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DescifrarArchivoRequest {
    private Integer idArchivo;
    private String clavePersonal;
}
