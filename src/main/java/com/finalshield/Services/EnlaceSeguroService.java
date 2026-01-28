package com.finalshield.Services;


import com.finalshield.DTO.Correo.CorreoDTO;
import com.finalshield.Model.Correo;
import com.finalshield.Model.EnlaceSeguro;
import com.finalshield.Model.Usuario;

import java.util.Optional;

public interface EnlaceSeguroService {
    EnlaceSeguro generarEnlaceSeguro(Correo correo, Usuario receptor, String claveBase64);
    Optional<CorreoDTO> validarToken(String token, String correoUsuario);
}
