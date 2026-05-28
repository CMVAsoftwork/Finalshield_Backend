package com.finalshield.Services.Impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.finalshield.DTO.Chatbot.ChatRequestDTO;
import com.finalshield.DTO.Chatbot.ChatResponseDTO;
import com.finalshield.DTO.Chatbot.SuggestionResponseDTO;
import com.finalshield.Model.Chatbot.ChatRule;
import com.finalshield.Services.ChatbotService;
import com.finalshield.Model.Chatbot.Suggestion;
import jakarta.annotation.PostConstruct;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.util.*;
@Service
public class ChatbotServiceImpl implements ChatbotService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String JSON_FILE_PATH = "src/main/resources/chatbot-data.json";

    private List<ChatRule> rules = new ArrayList<>();
    private List<Suggestion> suggestions = new ArrayList<>();

    private Map<String, String> userNames = new HashMap<>();

    @PostConstruct
    public void loadData() {
        try {
            InputStream inputStream = new ClassPathResource("chatbot-data.json").getInputStream();
            JsonNode root = objectMapper.readTree(inputStream);

            rules = objectMapper.convertValue(root.get("rules"), new TypeReference<List<ChatRule>>() {});

            if (root.has("suggestions")) {
                suggestions = objectMapper.convertValue(root.get("suggestions"), new TypeReference<List<Suggestion>>() {});
            }

            System.out.println("JSON cargado correctamente | Keywords: " + rules.size() + " | Sugerencias: " + suggestions.size());
        } catch (Exception e) {
            System.err.println("Error al cargar el JSON");
            e.printStackTrace();
        }
    }

    @Override
    public List<SuggestionResponseDTO> getSuggetions() {
        List<SuggestionResponseDTO> dtos=new ArrayList<>();
        suggestions.sort((s1, s2) ->Integer.compare(s2.getCount(), s1.getCount()));

        for (Suggestion suggestion : suggestions) {
            SuggestionResponseDTO dto= new SuggestionResponseDTO();
            dto.setMessage(suggestion.getMessage());
            dto.setCount(suggestion.getCount());
            dto.setLastAsked(suggestion.getLastAsked());
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public ChatResponseDTO getResponse(ChatRequestDTO request) {
        String userId = "default";
        String originalMessage = request.getMessage();
        String message = normalize(originalMessage);

        if (message.contains("como me llamo") || message.contains("cual es mi nombre")) {
            String name = userNames.get(userId);
            if (name != null) {
                return new ChatResponseDTO("Tu nombre es " + name);
            } else {
                return new ChatResponseDTO("Aún no me dices tu nombre 😭");
            }
        }

        if (message.contains("me llamo")) {
            String name = message.replace("me llamo", "").trim();
            userNames.put(userId, name);
            return new ChatResponseDTO("Mucho gusto " + name);
        }

        // Búsqueda de reglas
        for (ChatRule rule : rules) {
            for (String keyword : rule.getKeywords()) {
                if (message.contains(normalize(keyword))) {
                    List<String> responses = rule.getResponses();
                    int random = (int) (Math.random() * responses.size());
                    return new ChatResponseDTO(responses.get(random));
                }
            }
        }

        saveSuggestion(originalMessage);

        List<String> fallbackResponses = List.of(
                "No entendí esa solicitud 😭",
                "¿Podrías reformular tu pregunta?",
                "Aún estoy aprendiendo esa información",
                "No tengo información sobre eso todavía"
        );
        int random = (int) (Math.random() * fallbackResponses.size());
        return new ChatResponseDTO(fallbackResponses.get(random));
    }

    private void saveSuggestion(String message) {
        for (Suggestion s : suggestions) {
            if (s.getMessage().equalsIgnoreCase(message)) {
                s.incrementCount();
                saveToJson();
                return;
            }
        }

        Suggestion newSuggestion = new Suggestion(message);
        suggestions.add(newSuggestion);
        saveToJson();
    }

    private void saveToJson() {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("rules", rules);
            data.put("suggestions", suggestions);

            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(JSON_FILE_PATH), data);

            System.out.println("JSON actualizado | Sugerencias totales: " + suggestions.size());
        } catch (Exception e) {
            System.err.println(" Error al guardar el JSON");
            e.printStackTrace();
        }
    }

    private String normalize(String text) {
        return text.toLowerCase().trim()
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u");
    }
}