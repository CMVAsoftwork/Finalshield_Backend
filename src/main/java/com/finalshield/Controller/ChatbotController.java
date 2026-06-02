package com.finalshield.Controller;

import com.finalshield.DTO.Chatbot.ChatHistoryDTO;
import com.finalshield.DTO.Chatbot.ChatRequestDTO;
import com.finalshield.DTO.Chatbot.ChatResponseDTO;
import com.finalshield.DTO.Chatbot.SuggestionResponseDTO;
import com.finalshield.Services.ChatbotService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("api/chat")
public class ChatbotController
{
    @Autowired
    private ChatbotService chatbotService;

    @PostMapping
    private ChatResponseDTO chatResponseDTO(@RequestBody ChatRequestDTO request, HttpServletRequest servletRequest)
    {
	System.out.println(
            "AUTH HEADER = "
            + servletRequest.getHeader("Authorization")
    	);
        return chatbotService.getResponse(request);
    }

    @GetMapping("/suggestion")
    public ResponseEntity<List<SuggestionResponseDTO>>
    getSuggestions()
    {
        return ResponseEntity.ok(
                chatbotService.getSuggestions()
        );
    }

    @GetMapping("/history")
    public ResponseEntity<List<ChatHistoryDTO>> getHistory()
    {
        return  ResponseEntity.ok(
			chatbotService.getHistory()
			);
    }

}
