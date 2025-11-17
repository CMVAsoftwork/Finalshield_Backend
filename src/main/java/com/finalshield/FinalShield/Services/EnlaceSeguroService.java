package com.finalshield.FinalShield.Services;


import com.finalshield.FinalShield.DTO.Correo.CorreoDTO;
import com.finalshield.FinalShield.Model.Correo;
import com.finalshield.FinalShield.Model.EnlaceSeguro;
import com.finalshield.FinalShield.Model.Usuario;

import java.util.Optional;

public interface EnlaceSeguroService {
    EnlaceSeguro generarEnlaceSeguro(Correo correo, Usuario receptor, String claveBase64);
    Optional<CorreoDTO> validarToken(String token, String correoUsuario);
}
