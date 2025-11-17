package com.finalshield.FinalShield.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "archivos")
public class Archivo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idArchivo;

    @Column(nullable = false)
    private String nombreArchivo;

    @Column(nullable = false)
    private String estado;

    private String tipoArchivo;

    @Column(nullable = false)
    private String rutaArchivo;

    private long tamano;

    private LocalDateTime fechaSubida;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_carpeta_monitorizada")
    private CarpetaMonitorizada carpetaMonitorizada;
}
