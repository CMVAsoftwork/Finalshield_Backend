package com.finalshield.FinalShield.Services;

import com.finalshield.FinalShield.DTO.Chatbot.ChatRequestDTO;
import com.finalshield.FinalShield.DTO.Chatbot.ChatResponseDTO;
public interface ChatbotService
{

    ChatResponseDTO getResponse(ChatRequestDTO request);
}
