package com.finalshield.Controller;

import com.finalshield.DTO.Sesion.*;
import com.finalshield.Model.Usuario;
import com.finalshield.Repositorios.UsuarioRepositorio;
import com.finalshield.Security.JwtTokenProvider;
import com.finalshield.Services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UsuarioService ServeUsuario;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/registro")
    public ResponseEntity<LoginResponse> registrar(@Valid @RequestBody RegistroRequest request) {
        return ResponseEntity.ok(ServeUsuario.registrar(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ServeUsuario.login(request));
    }

    @PostMapping("/cambiar-contrasena")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> cambiarContrasena(@RequestBody CambiarContraseñaRequest request) {
        // Al usar @Data, Lombok crea getCorreo(), getContrasenaActual(), etc.
        ServeUsuario.cambiarContrasena(
                request.getCorreo(),
                request.getContrasenaActual(),
                request.getNuevaContrasena()
        );
        return ResponseEntity.ok("Contraseña actualizada correctamente.");
    }

    @PostMapping("/recuperar-contrasena")
    public ResponseEntity<?> recuperar(@RequestBody RecuperarContraseñaRequest req) {
        ServeUsuario.recuperarContrasenaSinToken(req.getCorreo(), req.getContrasenaActual(), req.getNuevaContrasena());
        return ResponseEntity.ok("Contraseña actualizada exitosamente.");
    }

    @PostMapping("/habilitar-biometrico")
    public ResponseEntity<?> habilitarBiometrico(@RequestBody HabilitarBiometricoRequest request) {
        String correo = request.getCorreo();
        Boolean huella = request.getHuella();

        if (huella == null) {
            return ResponseEntity.badRequest().body("El campo 'huella' es requerido.");
        }

        Usuario usuario = usuarioRepositorio.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setHuella(huella);
        usuarioRepositorio.save(usuario);

        return ResponseEntity.ok("Biometría actualizada");
    }

    @PostMapping("/login-bio")
    public ResponseEntity<LoginResponse> loginBiometrico(@RequestBody LoginBioRequest request) {
        Usuario usuario = usuarioRepositorio.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!usuario.getHuella()) {
            return ResponseEntity.status(403).body(null);
        }

        LoginResponse response = ServeUsuario.loginBiometrico(request.getCorreo());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/biometrico-activo/{correo}")
    public ResponseEntity<Boolean> isBiometricoActivo(@PathVariable String correo) {
        return ResponseEntity.ok(ServeUsuario.isBiometricoActivo(correo));
    }
}

