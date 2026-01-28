package com.finalshield.Services;

import com.finalshield.DTO.Correo.CorreoRequest;
import com.finalshield.DTO.Correo.EnvioCorreoDTO;
import com.finalshield.DTO.Correo.RecepcionCorreoDTO;
import com.finalshield.Model.Correo;
import com.finalshield.Model.EnlaceSeguro;
import com.finalshield.Model.Usuario;
import jakarta.mail.MessagingException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CorreoService {
    EnlaceSeguro enviarCorreoConCifrado(CorreoRequest dto, List<MultipartFile> adjuntos) throws Exception;
    void enviarCorreoNotificacion(Usuario emisor, Usuario receptor, Correo correo, EnlaceSeguro enlace) throws MessagingException;
    List<EnvioCorreoDTO> listarCorreosEnviados(String correoUsuario);
    List<RecepcionCorreoDTO> listarCorreosRecibidos(String correoUsuario);
    byte[] descifrarAdjuntoCorreo(Integer idArchivoCorreo, String claveBase64) throws Exception;
}
