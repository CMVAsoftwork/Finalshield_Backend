package com.finalshield.Services;

import com.finalshield.DTO.Chatbot.ChatHistoryDTO;
import com.finalshield.DTO.Chatbot.ChatRequestDTO;
import com.finalshield.DTO.Chatbot.ChatResponseDTO;
import com.finalshield.DTO.Chatbot.SuggestionResponseDTO;

import java.util.List;

public interface ChatbotService
{
    ChatResponseDTO getResponse(ChatRequestDTO request);

    List<SuggestionResponseDTO> getSuggestions();

    List<ChatHistoryDTO> getHistory(String username);

}
