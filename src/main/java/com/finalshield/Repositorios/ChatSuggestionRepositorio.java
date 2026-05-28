package com.finalshield.Repositorios;

import com.finalshield.Model.Chatbot.ChatSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ChatSuggestionRepositorio extends JpaRepository<ChatSuggestion, Integer> {
    Optional<ChatSuggestion>findByMensajeIgnoreCase(String mensaje);

    List<ChatSuggestion>findAllByOrderByContador();
}
