package com.finalshield.Controller;

import com.finalshield.DTO.Estadistica.EstadisticasResumenDTO;
import com.finalshield.Services.EstadisticasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/estadisticas")
public class EstadisticasController {

    @Autowired
    private EstadisticasService estadisticasService;

    @GetMapping("/resumen")
    public EstadisticasResumenDTO obtenerResumen() {
        return estadisticasService.obtenerResumen();
    }
}
