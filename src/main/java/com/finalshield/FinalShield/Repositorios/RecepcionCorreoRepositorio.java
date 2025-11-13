package com.finalshield.FinalShield.Repositorios;

import com.finalshield.FinalShield.Model.EnvioCorreo;
import com.finalshield.FinalShield.Model.RecepcionCorreo;
import com.finalshield.FinalShield.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecepcionCorreoRepositorio extends JpaRepository<RecepcionCorreo, Integer> {
    Optional<RecepcionCorreo> findByFechaRecepcion(LocalDateTime fechaRecepcion);
    Optional<RecepcionCorreo> findByEnvioRecepcion(EnvioCorreo envioRecepcion);
    Optional<RecepcionCorreo> findByUsuarioRecepcion(Usuario usuarioRecepcion);
    List<RecepcionCorreo> findByUsuarioRecepcionOrderByFechaRecepcionDesc(Usuario usuarioRecepcion);
}
