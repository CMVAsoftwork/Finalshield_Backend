package com.finalshield.Services;

import com.finalshield.DTO.Sesion.LoginRequest;
import com.finalshield.DTO.Sesion.LoginResponse;
import com.finalshield.DTO.Sesion.RegistroRequest;
import com.finalshield.Model.Usuario;

import javax.crypto.SecretKey;
import java.util.Optional;

public interface UsuarioService {
    Optional<Usuario> obtenerPorCorreo(String correo);
    LoginResponse registrar(RegistroRequest request);
    LoginResponse login(LoginRequest request);
    SecretKey recuperarClaveAES(Integer idUsuario, String contrasena);
    void cambiarContrasena(String correo, String contrasenaActual, String nuevaContrasena);
    void recuperarContrasenaSinToken(String correo, String contrasenaActual, String nuevaContrasena);
    Boolean isBiometricoActivo(String correo);
    LoginResponse loginBiometrico(String correo);
}
