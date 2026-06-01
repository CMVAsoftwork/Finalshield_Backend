package com.finalshield.Services;

import com.finalshield.Auditoria.AuditoriaEventoTipo;
import com.finalshield.Model.AuditoriaEvento;

import java.util.List;

public interface AuditoriaEventoService {
    void registrarEvento(Integer usuarioId,
                         AuditoriaEventoTipo tipoEvento,
                         String descripcion,
                         Boolean exitoso);

    void registrarEventoCompleto(Integer usuarioId,
                                 AuditoriaEventoTipo tipoEvento,
                                 String descripcion,
                                 String direccionIp,
                                 String dispositivo,
                                 Boolean exitoso);

    List<AuditoriaEvento> obtenerEventosPorUsuario(Integer usuarioId);
    List<AuditoriaEvento> obtenerTodos();
}
