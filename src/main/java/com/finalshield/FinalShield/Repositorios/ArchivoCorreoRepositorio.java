package com.finalshield.FinalShield.Repositorios;

import com.finalshield.FinalShield.Model.ArchivoCorreo;
import com.finalshield.FinalShield.Model.Correo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArchivoCorreoRepositorio extends JpaRepository<ArchivoCorreo, Integer> {
    List<ArchivoCorreo> findByCorreo(Correo correo);
    List<ArchivoCorreo> findByCorreo_IdCorreo(Integer idCorreo);
}
