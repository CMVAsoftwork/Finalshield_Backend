package com.finalshield.FinalShield.DTO.Correo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CorreoRequest {
    private String asunto;
    private String cuerpoPlano;
    private String destinatario;
    private String emisor;
}
