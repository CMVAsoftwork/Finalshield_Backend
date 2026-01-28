package com.finalshield.DTO.Carpeta;

import com.finalshield.DTO.Sesion.UsuarioDTO;
import com.finalshield.Model.Archivo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ArchivoDTO {
    private Integer idArchivo;
    private String nombreArchivo, estado,tipoArchivo;
    private String rutaArchivo;
    private long tamano;
    private LocalDateTime fechaSubida;

    private UsuarioDTO usuarioDTO;
    private CarpetaMonitorizadaDTO carpetaMonitorizadaDTO;

    public ArchivoDTO(Archivo p_archivo) {
        this.idArchivo = p_archivo.getIdArchivo();
        this.nombreArchivo = p_archivo.getNombreArchivo();
        this.estado = p_archivo.getEstado();
        this.tipoArchivo = p_archivo.getTipoArchivo();
        this.rutaArchivo = p_archivo.getRutaArchivo();
        this.tamano = p_archivo.getTamano();
        this.fechaSubida = p_archivo.getFechaSubida();
        usuarioDTO = new UsuarioDTO(p_archivo.getUsuario());
        carpetaMonitorizadaDTO = new CarpetaMonitorizadaDTO(p_archivo.getCarpetaMonitorizada());
    }
}
