package com.finalshield.Services;

import com.finalshield.Auditoria.AuditoriaEventoTipo;

public interface AuditoriaEventoLogsService {
    void logEvent(
            Long usuarioId,
            AuditoriaEventoTipo eventType,
            String descripcion,
            String ipAddress
    );
}
