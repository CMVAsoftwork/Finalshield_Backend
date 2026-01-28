package com.finalshield.DTO.Correo;

import com.finalshield.DTO.Sesion.UsuarioDTO;
import com.finalshield.Model.RecepcionCorreo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class RecepcionCorreoDTO {
    private Integer idRecepcionCorreo;
    private LocalDateTime fechaRecepcion;
    private UsuarioDTO usuarioRecepcionDTO;
    private EnvioCorreoDTO envioRecepcionDTO;

    public RecepcionCorreoDTO(RecepcionCorreo p_recepcionCorreo) {
        this.idRecepcionCorreo = p_recepcionCorreo.getIdRecepcionCorreo();
        this.fechaRecepcion = p_recepcionCorreo.getFechaRecepcion();
        usuarioRecepcionDTO = new UsuarioDTO(p_recepcionCorreo.getUsuarioRecepcion());
        envioRecepcionDTO = new EnvioCorreoDTO(p_recepcionCorreo.getEnvioRecepcion());
    }
}
