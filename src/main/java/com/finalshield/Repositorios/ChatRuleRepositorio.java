package com.finalshield.Repositorios;

import com.finalshield.Model.Chatbot.ChatRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatRuleRepositorio extends JpaRepository<ChatRule, Integer> {
    List<ChatRule> findByActivaTrue();
}
