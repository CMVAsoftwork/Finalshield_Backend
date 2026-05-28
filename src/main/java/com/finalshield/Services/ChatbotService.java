package com.finalshield.Services;

import com.finalshield.DTO.Chatbot.ChatRequestDTO;
import com.finalshield.DTO.Chatbot.ChatResponseDTO;
import com.finalshield.DTO.Chatbot.SuggestionResponseDTO;
import java.util.List;
public interface ChatbotService
{

    List<SuggestionResponseDTO> getSuggetions();
    ChatResponseDTO getResponse(ChatRequestDTO request);
}
