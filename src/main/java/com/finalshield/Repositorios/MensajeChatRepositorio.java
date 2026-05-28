package com.finalshield.Repositorios;

import com.finalshield.Model.Chatbot.ChatMessageBD;
import com.finalshield.Model.Usuario;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

@Repository
public interface MensajeChatRepositorio {
    List<ChatMessageBD> findByUsuario(Usuario usuario);
}
