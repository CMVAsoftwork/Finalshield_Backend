package com.finalshield.Repositorios;

import com.finalshield.Model.Chatbot.ChatRuleBD;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatRuleRepositorio {
    List<ChatRuleBD> findByActiva();
}
