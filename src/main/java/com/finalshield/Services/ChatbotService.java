package com.finalshield.FinalShield.Services;

import com.finalshield.DTO.Chatbot.ChatRequestDTO;
import com.finalshield.DTO.Chatbot.ChatResponseDTO;
public interface ChatbotService
{

    ChatResponseDTO getResponse(ChatRequestDTO request);
}
