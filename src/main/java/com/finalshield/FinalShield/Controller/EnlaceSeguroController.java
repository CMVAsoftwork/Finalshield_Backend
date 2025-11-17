package com.finalshield.FinalShield.Controller;

import com.finalshield.FinalShield.DTO.Correo.CorreoDTO;
import com.finalshield.FinalShield.Model.EnlaceSeguro;
import com.finalshield.FinalShield.Repositorios.EnlaceSeguroRepositorio;
import com.finalshield.FinalShield.Services.EnlaceSeguroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/enlaces")
public class EnlaceSeguroController {
    @Autowired
    private EnlaceSeguroService enlaceSeguroService;

    @Autowired
    private EnlaceSeguroRepositorio RepoEnlaceSeguro;

    @GetMapping("/{token}/validar")
    public ResponseEntity<String> validarTokenAntesDeLogin(@PathVariable String token) {
        Optional<EnlaceSeguro> optional = RepoEnlaceSeguro.findByTokenUnico(token);

        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.parseMediaType("text/html; charset=UTF-8"))
                    .body("<h2 style='color:red;'> Enlace no encontrado</h2><p>Verifica que el enlace sea correcto.</p>");
        }

        String deepLinkUri = "fileshield://verclave?security_token=" + token;
        EnlaceSeguro enlace = optional.get();

        if (enlace.isUsado()) {
            return ResponseEntity.status(HttpStatus.GONE)
                    .contentType(MediaType.parseMediaType("text/html; charset=UTF-8"))
                    .body("<h2 style='color:orange;'>️ Enlace ya utilizado</h2><p>Este enlace ya fue usado para recuperar una clave.</p>");
        }

        if (enlace.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.GONE)
                    .contentType(MediaType.parseMediaType("text/html; charset=UTF-8"))
                    .body("<h2 style='color:gray;'> Enlace expirado</h2><p>Este enlace ha caducado. Solicita uno nuevo desde la aplicación.</p>");
        }

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, deepLinkUri)
                .build();
    }

    @GetMapping("/logo")
    public ResponseEntity<Resource> logo() throws IOException {
        Resource resource = new ClassPathResource("static/Logo.svg");
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("image/svg+xml"))
                .body(resource);
    }

    @GetMapping("/{token}")
    public ResponseEntity<?> accederClave(@PathVariable String token, @RequestParam String correoUsuario) {
        Optional<CorreoDTO> optionalCorreoDTO = enlaceSeguroService.validarToken(token, correoUsuario);

        if (optionalCorreoDTO.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token inválido, expirado o acceso no autorizado para este correo.");
        }
        CorreoDTO correoDTO = optionalCorreoDTO.get();
        return ResponseEntity.ok(correoDTO);
    }
}
