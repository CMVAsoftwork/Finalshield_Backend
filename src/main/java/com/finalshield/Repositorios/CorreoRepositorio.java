package com.finalshield.Repositorios;

import com.finalshield.Model.Correo;
import com.finalshield.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CorreoRepositorio extends JpaRepository<Correo, Integer> {
    Optional<Correo> findByEstatus(String estatus);
    long countByEstatus(String estatus);

    // En EnvioCorreoRepository.java
    @Query("""
    SELECT CAST(ec.fechaEnvio AS date), COUNT(ec)
    FROM EnvioCorreo ec
    WHERE ec.usuarioEmisor = :usuario
    GROUP BY CAST(ec.fechaEnvio AS date)
""")
    List<Object[]> contarCorreosEnviadosPorFecha(@Param("usuario") Usuario usuario);
}
