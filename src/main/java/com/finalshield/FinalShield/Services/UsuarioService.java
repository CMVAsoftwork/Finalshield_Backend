package com.finalshield.FinalShield.Services;

import com.finalshield.FinalShield.DTO.Sesion.LoginRequest;
import com.finalshield.FinalShield.DTO.Sesion.LoginResponse;
import com.finalshield.FinalShield.DTO.Sesion.RegistroRequest;
import com.finalshield.FinalShield.Model.Usuario;

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
