package com.finalshield.Repositorios;

import com.finalshield.DTO.Estadistica.ActividadArchivoDTO;
import com.finalshield.Model.Archivo;
import com.finalshield.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    Optional<Archivo> findByIdArchivoAndUsuario(Integer idArchivo, Usuario usuario);
    List<Archivo> findByUsuario(Usuario usuario);
    long countByUsuarioAndEstado(Usuario usuario, String estado);

    // En ArchivoRepository.java
    @Query("SELECT new com.finalshield.DTO.Estadistica.ActividadArchivoDTO(" +
            "CAST(a.fechaSubida AS date), " +
            "SUM(CASE WHEN a.estado = 'CIFRADO' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN a.estado = 'DESCIFRADO' THEN 1 ELSE 0 END), 0L) " +
            "FROM Archivo a WHERE a.usuario = :usuario " +
            "GROUP BY CAST(a.fechaSubida AS date) " +
            "ORDER BY CAST(a.fechaSubida AS date) ASC")
    List<ActividadArchivoDTO> actividadArchivosPorDia(@Param("usuario") Usuario usuario);
}
