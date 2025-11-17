package com.finalshield.FinalShield.DTO.Sesion;

import com.finalshield.FinalShield.Model.Usuario;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsuarioDTO {
    private Integer idUsuario;
    private String nombre,apellidoP,apellidoM,claveCifDesPersonal,correo;

    public UsuarioDTO(Usuario p_Usuario) {
        this.idUsuario = p_Usuario.getIdUsuario();
        this.nombre = p_Usuario.getNombre();
        this.apellidoP = p_Usuario.getApellidoP();
        this.apellidoM = p_Usuario.getApellidoM();
        this.claveCifDesPersonal = p_Usuario.getClaveCifDesPersonal();
        this.correo = p_Usuario.getCorreo();
    }
}
