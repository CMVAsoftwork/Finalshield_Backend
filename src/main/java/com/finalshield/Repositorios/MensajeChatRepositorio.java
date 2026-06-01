package com.finalshield.Repositorios;

import com.finalshield.Model.Chatbot.ChatMessage;
import com.finalshield.Model.Usuario;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeChatRepositorio {
    List<ChatMessage> findByUsuario(Usuario usuario);
}
