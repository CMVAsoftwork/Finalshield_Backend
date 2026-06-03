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
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${groq.api.key}")
    private String groqApiKey;

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

        Usuario usuario=(Usuario) auth.getPrincipal();

        Integer userId=usuario.getIdUsuario();



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

	saveSuggestion(originalMessage);

        try {

		long inicio=System.currentTimeMillis();
            String aiResponse=askGroq(originalMessage);
	    long fin=System.currentTimeMillis();

	    System.out.println("GROQ tardo "+(fin-inicio)+"ms");
            saveChat(originalMessage, aiResponse, false, userId, fecha);

            return new ChatResponseDTO(aiResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }

                return new ChatResponseDTO("No tengo información sobre eso todavía");
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
    public List<ChatHistoryDTO> getHistory() {

	Authentication auth= SecurityContextHolder.getContext().getAuthentication();
	Usuario usuario=(Usuario) auth.getPrincipal();    

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

    private String askGroq(String message)
{

    try {
        String prompt = """
        Eres FinalBot, un asistente de ciberseguridad dentro de una app llamada FinalShield de cifrado de archivos.
        Responde de forma clara, breve y útil para estudiantes de programación.

        Usuario: %s
        """.formatted(message);

	String safePrompt=prompt
		.replace("\"", "\\\"")
		.replace("\n", "\\n");
	

        String jsonBody = """
        {
          "model": "llama-3.3-70b-versatile",
          "messages": [
            {
              "role": "user",
              "content": "%s"
            }
          ],
          "temperature": 0.7
        }
        """.formatted(safePrompt);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.groq.com/openai/v1/chat/completions"))
                .header("Authorization", "Bearer "+groqApiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );



        JsonNode root = objectMapper.readTree(response.body());
	
	JsonNode choices=root.path("choices");

	if(!choices.isArray() || choices.isEmpty())
	{
		return "Sin respuesta del modelo";
	}

        return choices.get(0)
                .path("message")
                .path("content")
                .asText();

    } catch (Exception e) {
        e.printStackTrace();
        return "No se pudo conectar con Groq ";
    }
}
}
