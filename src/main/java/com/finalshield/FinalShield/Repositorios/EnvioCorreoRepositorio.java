package com.finalshield.FinalShield.Repositorios;

import com.finalshield.FinalShield.Model.Correo;
import com.finalshield.FinalShield.Model.EnvioCorreo;
import com.finalshield.FinalShield.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnvioCorreoRepositorio extends JpaRepository<EnvioCorreo, Integer> {
    Optional<EnvioCorreo> findByFechaEnvio(LocalDateTime fechaEnvio);
    Optional<EnvioCorreo> findByCorreo(Correo correo);
    Optional<EnvioCorreo> findByUsuarioEmisor(Usuario usuarioEmisor);
    List<EnvioCorreo> findByUsuarioEmisorOrderByFechaEnvioDesc(Usuario usuarioEmisor);
}
