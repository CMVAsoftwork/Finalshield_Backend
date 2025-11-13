package com.finalshield.FinalShield.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
public class EnvioCorreo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEnvioCorreo;
    private LocalDateTime fechaEnvio;

    @ManyToOne
    private Usuario usuarioEmisor;
    @OneToOne
    @JoinColumn(name = "correo_id")
    private Correo correo;
}
