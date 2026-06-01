package com.finalshield.Repositorios;

import com.finalshield.Model.Chatbot.ChatMessage;
import com.finalshield.Model.Chatbot.ChatSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepositorio extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByUsuarioIdUsuarioOrderByFechaCreacionAsc(Integer userId);
    Optional<ChatSuggestion> findByMensajeIgnoreCase(String mensaje);
}
