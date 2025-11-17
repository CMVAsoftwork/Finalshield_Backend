package com.finalshield.FinalShield.Controller;

import com.finalshield.FinalShield.DTO.Cifrado.DescifradoRequest;
import com.finalshield.FinalShield.Services.CifradorAESService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/api/cifrado")
public class CifradorController {

    @Autowired
    private CifradorAESService cifradorAESService;

    @PostMapping("/descifrar")
    public ResponseEntity<?> descifrarTexto(@RequestBody DescifradoRequest request) {
        try {
            SecretKey clave = cifradorAESService.base64AClave(request.getClaveBase64());
            String textoDescifrado = cifradorAESService.descifrarTexto(
                    request.getTextoCifradoBase64(),
                    clave
            );
            return ResponseEntity.ok(textoDescifrado);

        } catch (GeneralSecurityException e) {
            System.err.println("Error de seguridad al descifrar texto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error de seguridad al descifrar el mensaje: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error interno del servidor al descifrar texto: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error interno al intentar descifrar el mensaje.");
        }
    }
}
