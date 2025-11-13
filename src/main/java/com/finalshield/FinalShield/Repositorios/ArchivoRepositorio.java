package com.finalshield.FinalShield.Repositorios;

import com.finalshield.FinalShield.Model.Archivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArchivoRepositorio extends JpaRepository<Archivo, Integer> {
    Optional<Archivo> findByNombreArchivo(String nombreArchivo);
    Optional<Archivo> findByEstado(String estado);
    Optional<Archivo> findByNombreArchivoAndEstado(String nombreArchivo, String estado);
    Optional<Archivo> findByTamano(long tamano);
    Optional<Archivo> findByTipoArchivo(String tipoArchivo);
    Optional<Archivo> findByFechaSubida(LocalDateTime fechaSubida);
    List<Archivo> findByCarpetaMonitorizada_IdCarpetaMonitorizada(Integer idCarpetaMonitorizada);
    List<Archivo> findByUsuario_IdUsuario(Integer idUsuario);
    Optional<Archivo> findByRutaArchivo(String rutaArchivo);
}
