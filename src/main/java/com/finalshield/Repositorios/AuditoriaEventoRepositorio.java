package com.finalshield.Repositorios;

import com.finalshield.Auditoria.AuditoriaEventoTipo;
import com.finalshield.Model.AuditoriaEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AuditoriaEventoRepositorio extends JpaRepository<AuditoriaEvento, Integer> {
    List<AuditoriaEvento> findByUsuarioId(Integer usuarioId);
    long countByTipoEvento(AuditoriaEventoTipo tipoEvento);

}
