package com.finalshield.FinalShield.Model.Chatbot;

import java.util.List;

public class ChatRule
{
    private List<String> keywords;
    private List<String> responses;

    public ChatRule() {
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getResponses() {
        return responses;
    }

    public void setResponses(List<String> responses) {
        this.responses = responses;
    }
}
