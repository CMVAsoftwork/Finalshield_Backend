package com.finalshield.Services.Impl;

import com.finalshield.Auditoria.AuditoriaEventoTipo;
import com.finalshield.Model.AuditoriaEvento;
import com.finalshield.Repositorios.AuditoriaEventoRepositorio;
import com.finalshield.Services.AuditoriaEventoLogsService;
import com.finalshield.Services.AuditoriaEventoService;
import org.springframework.stereotype.Service;


@Service
public class AuditoriaEventoLogsServiceImpl
        implements AuditoriaEventoLogsService {

    private final AuditoriaEventoService auditoriaService;

    public AuditoriaEventoLogsServiceImpl(
            AuditoriaEventoService auditoriaService
    ) {
        this.auditoriaService = auditoriaService;
    }

    @Override
    public void logEvent(
            Long usuarioId,
            AuditoriaEventoTipo eventType,
            String descripcion,
            String ipAddress
    ) {

        auditoriaService.registrarEventoCompleto(
                usuarioId.intValue(),
                eventType,
                descripcion,
                ipAddress,
                "DESCONOCIDO",
                true
        );
    }
}
