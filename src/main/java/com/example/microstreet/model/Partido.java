package com.example.microstreet.model;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class Partido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String lugar;
    private LocalDateTime fechaHora;
    private Integer cuposDisponibles;
    private String descripcion;

}
