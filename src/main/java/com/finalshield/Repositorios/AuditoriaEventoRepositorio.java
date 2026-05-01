package com.finalshield.Repositorios;

import com.finalshield.Model.AuditoriaEvento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditoriaEventoRepositorio extends JpaRepository<AuditoriaEvento, Integer> {
    List<AuditoriaEvento> findByUsuarioId(Integer usuarioId);

}
