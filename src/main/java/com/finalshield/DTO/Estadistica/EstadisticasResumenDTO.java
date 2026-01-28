package com.finalshield.DTO.Estadistica;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstadisticasResumenDTO {
    private long archivosCifrados;
    private long archivosDescifrados;
    private long correosEnviados;
}
