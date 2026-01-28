package com.finalshield.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
public class RecepcionCorreo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRecepcionCorreo;
    private LocalDateTime fechaRecepcion;

    @ManyToOne
    private Usuario usuarioRecepcion;
    @ManyToOne
    private EnvioCorreo envioRecepcion;
}
