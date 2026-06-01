package com.finalshield.Services.Impl;

import com.finalshield.Auditoria.AuditoriaEventoTipo;
import com.finalshield.DTO.Estadistica.ActividadArchivoDTO;
import com.finalshield.DTO.Estadistica.EstadisticasResumenDTO;
import com.finalshield.Repositorios.AuditoriaEventoRepositorio;
import com.finalshield.Services.EstadisticasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstadisticasServiceImpl implements EstadisticasService {
    @Autowired
    private AuditoriaEventoRepositorio repo;

    @Override
    public EstadisticasResumenDTO obtenerResumen() {

        long cifrados = repo.countByTipoEvento(
                AuditoriaEventoTipo.FILE_ENCRYPTED);

        long descifrados = repo.countByTipoEvento(
                AuditoriaEventoTipo.FILE_DECRYPTED);

        long correos = repo.countByTipoEvento(
                AuditoriaEventoTipo.LINK_CREATED);

        return new EstadisticasResumenDTO(
                cifrados,
                descifrados,
                correos
        );
    }

    @Override
    public List<ActividadArchivoDTO> obtenerActividad() {
        return List.of();
    }
}
