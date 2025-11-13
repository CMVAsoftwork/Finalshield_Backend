package com.finalshield.FinalShield.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Correo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer idCorreo;
    private String contenidoCifrado, claveCifDes, estatus;

    @OneToOne(mappedBy = "correo")
    private EnvioCorreo envioCorreo;
}
