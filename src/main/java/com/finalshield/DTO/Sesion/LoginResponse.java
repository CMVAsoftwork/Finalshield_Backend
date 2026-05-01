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
    private String pinReal;
    private String pinSeguro;


    public LoginResponse(String token, String tipoToken, String correo, String nombre, Integer idUsuario, String claveCifDesPersonal, String pinReal, String pinSeguro) {
        this.token = token;
        this.tipoToken = tipoToken;
        this.correo = correo;
        this.nombre = nombre;
        this.idUsuario = idUsuario;
        this.claveCifDesPersonal = claveCifDesPersonal;
        this.pinReal = pinReal;
        this.pinSeguro = pinSeguro;
    }
}
