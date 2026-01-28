package com.finalshield.DTO.Correo;

import com.finalshield.DTO.Sesion.UsuarioDTO;
import com.finalshield.Model.EnvioCorreo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class EnvioCorreoDTO {
    private Integer idEnvioCorreo;
    private LocalDateTime fechaEnvio;
    private UsuarioDTO usuarioEmisorDTO;
    private CorreoDTO correoDTO;

    public EnvioCorreoDTO(EnvioCorreo p_envioCorreo) {
        this.idEnvioCorreo = p_envioCorreo.getIdEnvioCorreo();
        this.fechaEnvio = p_envioCorreo.getFechaEnvio();
        usuarioEmisorDTO = new UsuarioDTO(p_envioCorreo.getUsuarioEmisor());
        correoDTO = new CorreoDTO(p_envioCorreo.getCorreo());
    }

}
