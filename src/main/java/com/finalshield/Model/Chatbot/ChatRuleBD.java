package com.finalshield.Model.Chatbot;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name="chat_rules")
public class ChatRuleBD {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "JSOn")
    private String keywords;

    @Column(columnDefinition = "JSON")
    private String responses;

    private Boolean activa=true;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

}
