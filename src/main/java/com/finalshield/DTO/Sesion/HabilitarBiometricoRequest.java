package com.finalshield.DTO.Sesion;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HabilitarBiometricoRequest {
    private String correo;
    private Boolean huella;
}
