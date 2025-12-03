package com.finalshield.FinalShield.Controller;

import com.finalshield.FinalShield.DTO.Cifrado.DescifradoRequest;
import com.finalshield.FinalShield.Services.CifradorAESService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.GeneralSecurityException;
import java.util.Base64;

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

    @PostMapping(value = "/cifrar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> cifrarArchivo(@RequestPart("archivo") MultipartFile archivo, @RequestPart("claveBase64") String claveBase64) {
        File temp = null;

        try {
            temp = File.createTempFile("entrada", archivo.getOriginalFilename());
            archivo.transferTo(temp);

            SecretKey clave = cifradorAESService.base64AClave(claveBase64);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            cifradorAESService.cifrarArchivoStream(temp, outputStream, clave);

            return ResponseEntity.ok(
                    Base64.getEncoder().encodeToString(outputStream.toByteArray())
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al cifrar archivo: " + e.getMessage());

        } finally {
            if (temp != null && temp.exists()) temp.delete();
        }
    }
}
