package com.finalshield.DTO.Sesion;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private String tipoToken = "Bearer";
    private String correo;
    private String nombre;
    private Integer idUsuario;
    private String claveCifDesPersonal;

    public LoginResponse(String token, String correo, String nombre, Integer idUsuario, String claveCifDesPersonal) {
        this.token = token;
        this.correo = correo;
        this.nombre = nombre;
        this.idUsuario = idUsuario;
        this.claveCifDesPersonal = claveCifDesPersonal;
    }
}
