package com.finalshield.Model.Chatbot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Suggestion {
    private String message;
    private int count;
    private String lastAsked;

    public Suggestion(String message) {
        this.message = message;
        this.count = 1;
        this.lastAsked = LocalDate.now().toString();
    }

    public void incrementCount() {
        this.count++;
        this.lastAsked = LocalDate.now().toString();
    }
}
