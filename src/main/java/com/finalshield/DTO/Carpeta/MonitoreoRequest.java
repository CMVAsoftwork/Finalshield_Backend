package com.finalshield.DTO.Carpeta;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MonitoreoRequest {
    private Integer idUsuario;
    private String contrasena;
    private String ruta;
}
