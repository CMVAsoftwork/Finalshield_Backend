package com.finalshield.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
public class ArchivoEscaner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEscaneo;

    private String tipoEscaneo;
    private LocalDateTime fechaEscaneo;
    private String nombreArchivoFinal;
    private boolean cifradoExitoso;

    @ManyToOne
    private Archivo archivoCifrado;
    @ManyToOne
    private Usuario usuario;
}
