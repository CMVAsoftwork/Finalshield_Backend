package com.finalshield.DTO.Sesion;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambiarContraseñaRequest {
    @NotBlank(message = "El correo es necesario")
    private String correo;
    @NotBlank(message = "Es necesario ingresar su contraseña")
    private String contrasenaActual;
    @NotBlank
    private String nuevaContrasena;
}
