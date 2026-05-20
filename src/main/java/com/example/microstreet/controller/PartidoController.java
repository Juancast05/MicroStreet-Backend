package com.example.microstreet.controller;

import com.example.microstreet.model.Partido;
import com.example.microstreet.repository.PartidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/partidos")
@CrossOrigin(origins = "*") // Permite la conexión desde Angular
public class PartidoController {

    @Autowired
    private PartidoRepository partidoRepository;

    // API GET PARA LISTAR LOS PARTIDOS
    @GetMapping
    public List<Partido> listarPartidos() {
        return partidoRepository.findAll();
    }

    // API POST PARA CREAR UN NUEVO PARTIDO (Con validación de 2 horas)
    @PostMapping
    public Partido crearPartido(@RequestBody Partido partido) {
        // Calculamos el rango de bloqueo: 2 horas antes y 2 horas después
        // Esto cubre el tiempo de juego (1h 40min) más el margen de cambio de equipos
        LocalDateTime horaInicioReserva = partido.getFechaHora().minusHours(2);
        LocalDateTime LocalDateTimeHoraFinReserva = partido.getFechaHora().plusHours(2);

        // Consultamos al repositorio si la cancha (lugar) ya está ocupada en ese rango
        boolean ocupado = partidoRepository.existeCruceDeHorario(
                partido.getLugar(),
                horaInicioReserva,
                LocalDateTimeHoraFinReserva
        );

        if (ocupado) {
            // Lanzamos un error 409 Conflict que Angular interceptará para mostrar la alerta
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La cancha ya está reservada en ese horario. Los partidos bloquean la cancha por 2 horas."
            );
        }

        return partidoRepository.save(partido);
    }

    @PutMapping("/{id}/unirse")
    public ResponseEntity<Partido> unirseAlPartido(@PathVariable Long id) {
        return partidoRepository.findById(id).map(partido -> {
            // Validamos que todavía queden cupos
            if (partido.getCuposDisponibles() > 0) {
                partido.setCuposDisponibles(partido.getCuposDisponibles() - 1);
                Partido actualizado = partidoRepository.save(partido);
                return ResponseEntity.ok(actualizado);
            } else {
                // Si no hay cupos, mandamos un error 400
                return ResponseEntity.badRequest().<Partido>build();
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    // NUEVO ENDPOINT: API DELETE PARA CANCELAR EL PARTIDO
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarPartido(@PathVariable Long id) {
        if (!partidoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        partidoRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}