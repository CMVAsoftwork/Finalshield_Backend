package com.finalshield.Services.Impl;

import com.finalshield.Auditoria.AuditoriaEventoTipo;
import com.finalshield.Model.AuditoriaEvento;
import com.finalshield.Repositorios.AuditoriaEventoRepositorio;
import com.finalshield.Services.AuditoriaEventoLogsService;
import org.springframework.stereotype.Service;


@Service
public class AuditoriaEventoLogsServiceImpl implements AuditoriaEventoLogsService {
    private final AuditoriaEventoRepositorio auditoriaRepositorio;

    public AuditoriaEventoLogsServiceImpl(AuditoriaEventoRepositorio auditoriaRepositorio) {
        this.auditoriaRepositorio = auditoriaRepositorio;
    }

    @Override
    public void logEvent(
            Long usuarioId,
            AuditoriaEventoTipo eventType,
            String descripcion,
            String ipAddress
    ) {

        AuditoriaEvento evento = new AuditoriaEvento();

        evento.setUsuarioId(usuarioId.intValue());
        evento.setTipoEvento(eventType.name());
        evento.setDescripcion(descripcion);
        evento.setDireccionIp(ipAddress);
        evento.setExitoso(true);

        auditoriaRepositorio.save(evento);
    }
}
