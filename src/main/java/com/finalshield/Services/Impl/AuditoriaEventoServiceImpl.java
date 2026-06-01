package com.finalshield.Services.Impl;

import com.finalshield.Auditoria.AuditoriaEventoTipo;
import com.finalshield.Model.AuditoriaEvento;
import com.finalshield.Repositorios.AuditoriaEventoRepositorio;
import com.finalshield.Services.AuditoriaEventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditoriaEventoServiceImpl implements AuditoriaEventoService {
    @Autowired
    private AuditoriaEventoRepositorio auditoriaRepo;

    @Override
    public void registrarEvento(Integer usuarioId,
                                AuditoriaEventoTipo tipoEvento,
                                String descripcion,
                                Boolean exitoso) {

        AuditoriaEvento evento = new AuditoriaEvento();

        evento.setUsuarioId(usuarioId);
        evento.setTipoEvento(tipoEvento);
        evento.setDescripcion(descripcion);
        evento.setExitoso(exitoso);

        auditoriaRepo.save(evento);
    }

    @Override
    public void registrarEventoCompleto(Integer usuarioId,
                                        AuditoriaEventoTipo tipoEvento,
                                        String descripcion,
                                        String direccionIp,
                                        String dispositivo,
                                        Boolean exitoso) {

        AuditoriaEvento evento = new AuditoriaEvento();

        evento.setUsuarioId(usuarioId);
        evento.setTipoEvento(tipoEvento);
        evento.setDescripcion(descripcion);
        evento.setDireccionIp(direccionIp);
        evento.setDispositivo(dispositivo);
        evento.setExitoso(exitoso);

        auditoriaRepo.save(evento);
    }

    @Override
    public List<AuditoriaEvento> obtenerEventosPorUsuario(Integer usuarioId) {
        return auditoriaRepo.findByUsuarioId(usuarioId);
    }

    @Override
    public List<AuditoriaEvento> obtenerTodos() {

        return auditoriaRepo.findAll();
    }

}
