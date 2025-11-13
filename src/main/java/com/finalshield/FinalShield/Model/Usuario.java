package com.finalshield.FinalShield.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;
    private String nombre,apellidoP,apellidoM,claveCifDesPersonal,contrasena,salt;
    private Boolean huella;
    @Column(unique = true, nullable = false)
    private String correo;

}
