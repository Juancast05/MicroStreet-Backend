package com.example.microstreet.repository;

import com.example.microstreet.model.Partido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public interface PartidoRepository extends JpaRepository<Partido, Long> {

    // Añade esta consulta para verificar los cruces de horarios en una misma cancha
    @Query("SELECT COUNT(p) > 0 FROM Partido p WHERE p.lugar = :lugar AND p.fechaHora < :horaFin AND p.fechaHora > :horaInicio")
    boolean existeCruceDeHorario(
            @Param("lugar") String lugar,
            @Param("horaInicio") LocalDateTime horaInicio,
            @Param("horaFin") LocalDateTime horaFin
    );
}