package com.finalshield.Controller;

import com.finalshield.DTO.Chatbot.ChatResponseDTO;
import com.finalshield.DTO.Chatbot.ChatRequestDTO;
import com.finalshield.DTO.Chatbot.SuggestionResponseDTO;
import com.finalshield.Services.ChatbotService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/suggestion")
    public ResponseEntity<List<SuggestionResponseDTO>> getSuggestions()
    {
        List<SuggestionResponseDTO> suggestions=chatService.getSuggetions();
        return ResponseEntity.ok(suggestions);
    }
}
