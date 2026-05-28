package com.finalshield.DTO.Chatbot;

public class SuggestionResponseDTO
{
    private String message;
    private int count;
    private String lastAsked;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getLastAsked() {
        return lastAsked;
    }

    public void setLastAsked(String lastAsked) {
        this.lastAsked = lastAsked;
    }
}
