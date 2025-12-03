package com.finalshield.FinalShield.DTO.Carpeta;

import com.finalshield.FinalShield.Model.Archivo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ArchivoResponseDTO {
    private Integer idArchivo;
    private String nombreArchivo;
    private String rutaArchivo;
    private String estado;
    private String tipoArchivo;
    private long tamano;
    private LocalDateTime fechaSubida;

    public ArchivoResponseDTO(Archivo a) {
        this.idArchivo = a.getIdArchivo();
        this.nombreArchivo = a.getNombreArchivo();
        this.rutaArchivo = a.getRutaArchivo();
        this.estado = a.getEstado();
        this.tipoArchivo = a.getTipoArchivo();
        this.tamano = a.getTamano();
        this.fechaSubida = a.getFechaSubida();
    }
}
