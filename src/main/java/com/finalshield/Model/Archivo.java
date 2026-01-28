package com.finalshield.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
public class Archivo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idArchivo;
    private String nombreArchivo, estado, tipoArchivo, rutaArchivo;
    private long tamano;
    private LocalDateTime fechaSubida;

    @ManyToOne
    private Usuario usuario;
    @ManyToOne
    private CarpetaMonitorizada carpetaMonitorizada;
}
