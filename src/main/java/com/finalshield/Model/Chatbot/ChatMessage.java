package com.finalshield.Model.Chatbot;

import com.finalshield.Model.Usuario;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name="chat_message")
public class ChatMessage
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="usuario_id", nullable = false)
    private Usuario usuario;

    @Column(columnDefinition = "TEXT")
    private String mensaje;

    @Column(columnDefinition = "TEXT")
    private String respuesta;

    private Boolean isFallback= false;
    private LocalDateTime fechaCreacion;

    @PrePersist
    public void PrePersist()
    {
        this.fechaCreacion=LocalDateTime.now();
    }
}
