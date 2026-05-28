package com.finalshield.DTO.Chatbot;

public class ChatResponseDTO
{
    private String response;

    public ChatResponseDTO(String response) {
        this.response = response;
    }

    public ChatResponseDTO() {

    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public ChatResponseDTO toChatResponseDto(String response)
    {
        return new ChatResponseDTO(response);
    }
}
