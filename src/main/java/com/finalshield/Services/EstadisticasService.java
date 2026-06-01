package com.finalshield.Services;

import com.finalshield.DTO.Estadistica.ActividadArchivoDTO;
import com.finalshield.DTO.Estadistica.EstadisticasResumenDTO;

import java.util.List;

public interface EstadisticasService {
    EstadisticasResumenDTO obtenerResumen();

    List<ActividadArchivoDTO> obtenerActividad();
}
