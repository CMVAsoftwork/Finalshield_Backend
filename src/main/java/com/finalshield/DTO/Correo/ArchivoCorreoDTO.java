package com.finalshield.DTO.Correo;

import com.finalshield.Model.ArchivoCorreo;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArchivoCorreoDTO {
    private Integer idArchivoCorreo;
    private String nombreOriginal;

    public ArchivoCorreoDTO(ArchivoCorreo archivoCorreo) {
        this.idArchivoCorreo = archivoCorreo.getIdArchivoCorreo();
        this.nombreOriginal = archivoCorreo.getNombreOriginal();
    }
}
