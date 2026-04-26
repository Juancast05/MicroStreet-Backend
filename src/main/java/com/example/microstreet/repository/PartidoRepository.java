package com.example.microstreet.repository;

import com.example.microstreet.model.Partido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartidoRepository extends JpaRepository<Partido, Long> {
    // Aquí ya hay métodos como save(), findAll(), delete() listos para usar
}