package com.finalshield.Controller;

import com.finalshield.DTO.Chatbot.ChatResponseDTO;
import com.finalshield.DTO.Chatbot.ChatRequestDTO;
import com.finalshield.Services.ChatbotService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatbotController
{
    @Autowired
    private ChatbotService chatService;

    @PostMapping
    public ChatResponseDTO chatResponse(@RequestBody ChatRequestDTO request)
    {
        return chatService.getResponse(request);
    }
}
