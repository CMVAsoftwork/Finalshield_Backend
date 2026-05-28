package com.finalshield.Model.Chatbot;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name="chat_suggestion")
public class ChatSuggestionBD {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 500, nullable = false)
    private String mensaje;

    private int contador=1;

    private LocalDateTime ultimaVezPreguntado;

    @Enumerated(EnumType.STRING)
    private EstadoSugerencia estado=EstadoSugerencia.PENDIENTE;
}
