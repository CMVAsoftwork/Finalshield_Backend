package com.finalshield.DTO.Estadistica;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ActividadArchivoDTO {
    private LocalDate fecha;
    private Long cifrados;
    private Long descifrados;
    private Long correos;

    public ActividadArchivoDTO(Object fecha, Long cifrados, Long descifrados, Long correos) {
        if (fecha instanceof java.sql.Date) {
            this.fecha = ((java.sql.Date) fecha).toLocalDate();
        } else {
            this.fecha = (LocalDate) fecha;
        }
        this.cifrados = cifrados;
        this.descifrados = descifrados;
        this.correos = correos;
    }
}
