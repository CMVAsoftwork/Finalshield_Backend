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

    @Column(length = 1000, nullable = false )
    private String mensaje;

    @Column(length = 1000)
    private String respuesta;

    private Boolean isFallback= false;
    private LocalDateTime fechaCreacion;

    @PrePersist
    public void PrePersist()
    {
        this.fechaCreacion=LocalDateTime.now();
    }
}
