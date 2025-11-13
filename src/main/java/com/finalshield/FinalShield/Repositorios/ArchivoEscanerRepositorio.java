package com.finalshield.FinalShield.Repositorios;

import com.finalshield.FinalShield.Model.ArchivoEscaner;
import com.finalshield.FinalShield.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArchivoEscanerRepositorio extends JpaRepository<ArchivoEscaner, Integer> {
    List<ArchivoEscaner> findByUsuario(Usuario usuario);
    List<ArchivoEscaner> findByTipoEscaneo(String tipoEscaneo);
    List<ArchivoEscaner> findByCifradoExitosoTrue();
    List<ArchivoEscaner> findByCifradoExitosoFalse();
    ArchivoEscaner findByNombreArchivoFinal(String nombreArchivoFinal);
}

