package com.finalshield.FinalShield.Repositorios;

import com.finalshield.FinalShield.Model.Correo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CorreoRepositorio extends JpaRepository<Correo, Integer> {
    Optional<Correo> findByEstatus(String estatus);
}
