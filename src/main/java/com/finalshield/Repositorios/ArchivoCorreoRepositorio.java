package com.finalshield.Repositorios;

import com.finalshield.Model.ArchivoCorreo;
import com.finalshield.Model.Correo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArchivoCorreoRepositorio extends JpaRepository<ArchivoCorreo, Integer> {
    List<ArchivoCorreo> findByCorreo(Correo correo);
    List<ArchivoCorreo> findByCorreo_IdCorreo(Integer idCorreo);
}
