package com.finalshield.Services.Impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.finalshield.DTO.Chatbot.ChatRequestDTO;
import com.finalshield.DTO.Chatbot.ChatResponseDTO;
import com.finalshield.Model.Chatbot.ChatRule;
import com.finalshield.Services.ChatbotService;

import jakarta.annotation.PostConstruct;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatbotServiceImpl implements ChatbotService {

    private List<ChatRule> rules= new ArrayList<>();
    private String username;

    @PostConstruct
    public void loadRules()
    {
        try
        {
            ObjectMapper mapper=new ObjectMapper();
            InputStream inputStream=new ClassPathResource("chatbot-data.json").getInputStream();
            rules= mapper.readValue(inputStream, new TypeReference<List<ChatRule>>() {
            });

            System.out.println("JSON cargado correctamente");
        }
        catch (Exception e)
        {
            System.out.println("Error al cargar el JSON");
            e.printStackTrace();
        }
    }

    @Override
    public ChatResponseDTO getResponse(ChatRequestDTO request) {
        String message= normalize(request.getMessage());

        if(message.contains("como me llamo") || message.contains("cual es mi nombre"))
        {
            if(username !=null)
            {
                return new ChatResponseDTO("Tu nombre es "+username);
            }
            else {
                return new ChatResponseDTO("Aun no me dices tu nombre 😭");
            }
        }

        if(message.contains("me llamo") || message.contains("nombre"))
        {
            String[] parts= message.split("me llamo");

            if(parts.length >1)
            {
                username = parts [1].trim();

                return new ChatResponseDTO("Mucho gusto "+ username);
            }
        }



        for(ChatRule rule: rules)
        {
            for (String keyword : rule.getKeywords())
            {
                if(message.contains(normalize(keyword)))
                {
                    List<String> responses= rule.getResponses();

                    int random=(int)(Math.random() * responses.size());
                    return new ChatResponseDTO(responses.get(random));
                }
            }
        }

        List<String>fallbackResponses=List.of(
                "No entendi esa solicitud 😭",
                "¿Podrías reformular tu pregunta?",
                "Aún estoy aprendiendo esa información",
                "No tengo información sobre eso todavía"
        );
        int random= (int)(Math.random() * fallbackResponses.size());
        return new ChatResponseDTO(fallbackResponses.get(random));
    }

    private String normalize(String text)
    {
        return text.toLowerCase().trim()
                .replace("á","a")
                .replace("é","e")
                .replace("í","i")
                .replace("ó","o")
                .replace("ú","u");
    }
}