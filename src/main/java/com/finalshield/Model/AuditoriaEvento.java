package com.finalshield.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_evento")
@Data
@NoArgsConstructor
public class AuditoriaEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(name = "tipo_evento", nullable = false)
    private String tipoEvento;

    private String descripcion;

    private LocalDateTime fecha;

    private String direccionIp;

    private String dispositivo;

    private Boolean exitoso;

    @PrePersist
    protected void onCreate() {
        this.fecha = LocalDateTime.now();
    }
}
