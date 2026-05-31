package com.finalshield.DTO.Chatbot;

import lombok.Data;

@Data
public class ChatResponseDTO
{
    private String response;

    public ChatResponseDTO(String response) {
        this.response = response;
    }

    public ChatResponseDTO() {

    }
    public ChatResponseDTO toChatResponseDto(String response)
    {
        return new ChatResponseDTO(response);
    }
}
