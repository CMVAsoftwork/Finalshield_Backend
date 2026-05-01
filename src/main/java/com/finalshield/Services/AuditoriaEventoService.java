package com.finalshield.Services;

import com.finalshield.Model.AuditoriaEvento;

import java.util.List;

public interface AuditoriaEventoService {
    void registrarEvento(Integer usuarioId,
                         String tipoEvento,
                         String descripcion,
                         Boolean exitoso);

    void registrarEventoCompleto(Integer usuarioId,
                                 String tipoEvento,
                                 String descripcion,
                                 String direccionIp,
                                 String dispositivo,
                                 Boolean exitoso);

    List<AuditoriaEvento> obtenerEventosPorUsuario(Integer usuarioId);
    List<AuditoriaEvento> obtenerTodos();
}
