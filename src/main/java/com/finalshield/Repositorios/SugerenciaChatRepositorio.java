package com.finalshield.Repositorios;

import com.finalshield.Model.Chatbot.ChatSuggestionBD;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.finalshield.Model.Chatbot.EstadoSugerencia;
import java.util.List;

@Repository
public interface SugerenciaChatRepositorio
{
    List<ChatSuggestionBD> findByEstado(EstadoSugerencia estado);

}
