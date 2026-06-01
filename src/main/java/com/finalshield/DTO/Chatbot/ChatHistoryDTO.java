package com.finalshield.DTO.Chatbot;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatHistoryDTO
{
    private String mensaje;
    private String respuesta;
    private Boolean fallback;
    private LocalDateTime fecha;
}
