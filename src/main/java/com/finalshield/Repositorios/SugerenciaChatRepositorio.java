package com.finalshield.Repositorios;

import com.finalshield.Model.Chatbot.ChatSuggestion;
import org.springframework.stereotype.Repository;
import com.finalshield.Model.Chatbot.EstadoSugerencia;
import java.util.List;

@Repository
public interface SugerenciaChatRepositorio
{
    List<ChatSuggestion> findByEstado(EstadoSugerencia estado);

}
