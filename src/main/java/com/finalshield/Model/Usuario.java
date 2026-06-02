package com.finalshield.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.finalshield.Model.Chatbot.ChatMessage;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString; // <-- Se agrega la importación de Lombok para el Exclude

import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;

    private String nombre, apellidoP, apellidoM, claveCifDesPersonal;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String contrasena;

    private Boolean huella;

    @Column(unique = true, nullable = false)
    private String pinRealHash;

    private String pinSeguroHash;
    private String correo;

    @ToString.Exclude
    @OneToMany(mappedBy = "usuario")
    private List<ChatMessage> mensajes;
}