package com.finalshield.Services.Impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import com.finalshield.DTO.Chatbot.ChatHistoryDTO;
import com.finalshield.DTO.Chatbot.ChatRequestDTO;
import com.finalshield.DTO.Chatbot.ChatResponseDTO;
import com.finalshield.DTO.Chatbot.SuggestionResponseDTO;
import com.finalshield.Model.Chatbot.ChatMessage;
import com.finalshield.Model.Chatbot.ChatRule;
import com.finalshield.Model.Chatbot.ChatSuggestion;
import com.finalshield.Model.Usuario;
import com.finalshield.Repositorios.ChatMessageRepositorio;
import com.finalshield.Repositorios.ChatRuleRepositorio;
import com.finalshield.Repositorios.ChatSuggestionRepositorio;
import com.finalshield.Repositorios.UsuarioRepositorio;
import com.finalshield.Services.ChatbotService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;
@Service
public class ChatbotServiceImpl implements ChatbotService {

    @Autowired
    private ChatRuleRepositorio chatRuleRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private ChatMessageRepositorio chatMessageRepositorio;
    @Autowired
    private ChatSuggestionRepositorio chatSuggestionRepositorio;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ChatResponseDTO getResponse(ChatRequestDTO request)
    {
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        String username=auth.getName();
        System.out.println("Usuario JWT: "+username);
        Usuario usuario=usuarioRepositorio.findByNombre(username).orElseThrow(()-> new RuntimeException("Usuario no encontrado"));
        Integer userId =(usuario.getIdUsuario()) ;
        String originalMessage = request.getMessage();
        String message = normalize(originalMessage);
        String response;
        LocalDateTime fecha=LocalDateTime.now();


        List<String> matchedResponses = new ArrayList<>();

        try {

            List<ChatRule> rules =
                    chatRuleRepositorio.findByActivaTrue();

            for(ChatRule rule : rules)
            {
                List<String> keywords =
                        objectMapper.readValue(
                                rule.getKeywords(),
                                new TypeReference<List<String>>() {}
                        );

                List<String> responses =
                        objectMapper.readValue(
                                rule.getResponses(),
                                new TypeReference<List<String>>() {}
                        );

                for(String keyword : keywords)
                {
                    if(message.contains(normalize(keyword)))
                    {
                        matchedResponses.addAll(responses);
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if(!matchedResponses.isEmpty())
        {
            int random =
                    (int)(Math.random() * matchedResponses.size());

            response = matchedResponses.get(random);

            saveChat(originalMessage, response, false, userId, fecha);

            return new ChatResponseDTO(response);
        }

        try {
            String aiResponse=askGemini(originalMessage);
            saveChat(originalMessage, aiResponse, true, userId, fecha);
            return new ChatResponseDTO(aiResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }

        saveSuggestion(originalMessage);


        List<String> fallbackResponses = List.of(
                "No entendí esa solicitud 😭",
                "¿Podrías reformular tu pregunta?",
                "Aún estoy aprendiendo esa información",
                "No tengo información sobre eso todavía"
        );

        int random =
                (int)(Math.random() * fallbackResponses.size());
                response=fallbackResponses.get(random);
                return new ChatResponseDTO(response);
    }

    private void saveChat(String message, String response, boolean fallback, Integer userId, LocalDateTime fecha)
    {

        Usuario usuario= usuarioRepositorio.findById(userId).orElseThrow(()->new RuntimeException("Usuario no encontrado"));
        ChatMessage chatMessage=new ChatMessage();
        chatMessage.setMensaje(message);
        chatMessage.setRespuesta(response);
        chatMessage.setIsFallback(fallback);
        chatMessage.setUsuario(usuario);
        chatMessage.setFechaCreacion(fecha);

        chatMessageRepositorio.save(chatMessage);

    }

    @Override
    public List<SuggestionResponseDTO> getSuggestions()
    {
        List<ChatSuggestion> suggestions =
                chatSuggestionRepositorio
                        .findAllByOrderByContador();

        List<SuggestionResponseDTO> dtos =
                new ArrayList<>();

        for(ChatSuggestion suggestion : suggestions)
        {
            SuggestionResponseDTO dto =
                    new SuggestionResponseDTO();

            dto.setMessage(
                    suggestion.getMensaje()
            );

            dto.setCount(
                    suggestion.getContador()
            );

            dto.setLastAsked(
                    suggestion.getUltimaVezPreguntado().toString()
            );

            dtos.add(dto);
        }

        return dtos;
    }

    private void saveSuggestion(String message)
    {
        Optional<ChatSuggestion> existingSuggestion =
                chatSuggestionRepositorio
                        .findByMensajeIgnoreCase(message);

        if(existingSuggestion.isPresent())
        {
            ChatSuggestion suggestion =
                    existingSuggestion.get();

            suggestion.setContador(
                    suggestion.getContador() + 1
            );

            suggestion.setUltimaVezPreguntado(
                    LocalDateTime.now()
            );

            chatSuggestionRepositorio.save(suggestion);

            return;
        }

        ChatSuggestion newSuggestion =
                new ChatSuggestion();

        newSuggestion.setMensaje(message);

        newSuggestion.setContador(1);

        newSuggestion.setUltimaVezPreguntado(
                LocalDateTime.now()
        );

        chatSuggestionRepositorio.save(newSuggestion);
    }

    @Override
    public List<ChatHistoryDTO> getHistory(String username) {
        Usuario usuario= usuarioRepositorio.findByNombre(username).orElseThrow(()->new RuntimeException("Usuario no encontrado"));

        List<ChatMessage>messages=
                chatMessageRepositorio.findByUsuarioIdUsuarioOrderByFechaCreacionAsc(usuario.getIdUsuario());

        List<ChatHistoryDTO> result=new ArrayList<>();

        for(ChatMessage m: messages)
        {
            ChatHistoryDTO historyDTO = new ChatHistoryDTO();
            historyDTO.setFallback(m.getIsFallback());
            historyDTO.setFecha(m.getFechaCreacion());
            historyDTO.setMensaje(m.getMensaje());
            historyDTO.setRespuesta(m.getRespuesta());

            result.add(historyDTO);
        }
        return result;
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

    private final HttpClient httpClient= HttpClient.newHttpClient();

    private String askGemini(String message)
    {
        try{
            String prompt = """
            Eres FinalBot, un asistente de ciberseguridad dentro de una app llamada FinalShield de cifrado de archivos.
            Responde de forma clara, breve y útil para estudiantes de programación.
    
            Usuario: %s
            """.formatted(message);

            String jsonBody = """
            {
              "contents": [{
                "parts": [{
                  "text": "%s"
                }]
              }]
            }
            """.formatted(prompt);

            HttpRequest httpRequest=HttpRequest.newBuilder()
                    .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=TU_API_KEY"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            HttpResponse<String>response= httpClient.send(
                    httpRequest,
                    HttpResponse.BodyHandlers.ofString()
            );

            JsonNode root= objectMapper.readTree(response.body());

            return root
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();
        }catch (Exception e)
        {
            return ("No se pudo establecer la conexion con Gemini");
        }
    }
}
