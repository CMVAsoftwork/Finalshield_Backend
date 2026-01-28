package com.finalshield.Repositorios;

import com.finalshield.Model.CarpetaMonitorizada;
import com.finalshield.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarpetaMonitorizadaRepositorio extends JpaRepository<CarpetaMonitorizada, Integer> {
    Optional<CarpetaMonitorizada> findCarpetaMonitorizadaByRuta(String ruta);
    Optional<CarpetaMonitorizada> findCarpetaMonitorizadaByUsuario(Usuario usuario);
    Optional<CarpetaMonitorizada> findCarpetaMonitorizadaByRutaAndUsuario(String ruta, Usuario usuario);
    List<CarpetaMonitorizada> findByUsuarioIdUsuario(Integer idUsuario);
}
