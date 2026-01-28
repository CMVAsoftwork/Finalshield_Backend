package com.finalshield.DTO.Carpeta;

import com.finalshield.DTO.Sesion.UsuarioDTO;
import com.finalshield.Model.CarpetaMonitorizada;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CarpetaMonitorizadaDTO {
    private Integer idCarpetaMonitorizada;
    private String ruta;
    private UsuarioDTO usuarioDTO;

    private String estado;

    public CarpetaMonitorizadaDTO(CarpetaMonitorizada p_carpetaMonitorizada) {
        this.idCarpetaMonitorizada = p_carpetaMonitorizada.getIdCarpetaMonitorizada();
        this.ruta = p_carpetaMonitorizada.getRuta();
        this.usuarioDTO = new UsuarioDTO(p_carpetaMonitorizada.getUsuario());
        this.estado = "EN MONITOREO";
    }
}
