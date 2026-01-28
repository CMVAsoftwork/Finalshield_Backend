package com.finalshield.Controller;

import com.finalshield.DTO.Correo.CorreoDTO;
import com.finalshield.Model.EnlaceSeguro;
import com.finalshield.Repositorios.EnlaceSeguroRepositorio;
import com.finalshield.Services.EnlaceSeguroService;
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
    private EnlaceSeguroRepositorio enlaceSeguroRepositorio;
    @Autowired
    private EnlaceSeguroService enlaceSeguroService;

    @Autowired
    private EnlaceSeguroRepositorio RepoEnlaceSeguro;

    @GetMapping("/{token}/validar")
    public ResponseEntity<String> validarTokenAntesDeLogin(@PathVariable String token) {
        Optional<EnlaceSeguro> optional =enlaceSeguroRepositorio.findByTokenUnico(token);

        if (optional.isEmpty()) {
            return buildHtmlResponse("Enlace no encontrado", "Verifica que el enlace sea correcto.", "#FF4B4B");
        }

        EnlaceSeguro enlace = optional.get();

        if (enlace.isUsado()) {
            return buildHtmlResponse("Enlace ya utilizado", "Este enlace ya fue usado para recuperar una clave.", "#FFA500");
        }

        if (enlace.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            return buildHtmlResponse("Enlace expirado", "Este enlace ha caducado. Solicita uno nuevo.", "#888888");
        }

        String customSchemeUri = "fileshield://verclave?security_token=" + token;

        String html = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>FileShield Transfer</title>
        </head>
        <body style="background-color: #0B0F1A; color: white; font-family: sans-serif; text-align: center; padding: 100px 20px;">
            <div style="border: 1px solid #1E90FF; padding: 20px; border-radius: 10px; background: #111520;">
                <h2 style="color: #1E90FF;">🔐 Abrir mensaje en FileShield</h2>
                <p>Presiona el botón para descifrar tu contenido:</p>
                <br>
                <a href="%s" 
                   style="background-color: #1E90FF; color: white; padding: 15px 25px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block;">
                   ABRIR EN LA APLICACIÓN
                </a>
            </div>
        </body>
        </html>
        """.formatted(customSchemeUri, customSchemeUri);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }

    private ResponseEntity<String> buildHtmlResponse(String titulo, String mensaje, String color) {
        String html = String.format("""
            <body style='background:#0B0F1A; color:white; text-align:center; padding-top:50px; font-family:sans-serif;'>
                <h2 style='color:%s;'>%s</h2>
                <p>%s</p>
            </body>
            """, color, titulo, mensaje);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_HTML).body(html);
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
