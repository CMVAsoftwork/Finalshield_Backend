package com.finalshield.FinalShield.DTO.Sesion;

import jakarta.validation.constraints.NegativeOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginBioRequest {
    private String correo;
}
