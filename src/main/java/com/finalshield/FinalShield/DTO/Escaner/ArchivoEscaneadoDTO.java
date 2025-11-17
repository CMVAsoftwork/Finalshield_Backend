package com.finalshield.FinalShield.DTO.Escaner;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ArchivoEscaneadoDTO {
    private Integer idArchivoEcaneado;
    private String nombreArchivoEscaneado;
    private LocalDateTime fechaSubida;
    private String hashSha256;
}
