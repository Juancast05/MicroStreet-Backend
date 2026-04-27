package com.example.microstreet.controller;
import com.example.microstreet.model.Partido;
import com.example.microstreet.repository.PartidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/partidos")
@CrossOrigin(origins = "*") // ESTO HACE QUE ANGULAR SE PUEDA CONECTAR DESPUES
public class PartidoController {

    @Autowired
    private PartidoRepository partidoRepository;

    //API GET PARA LISTAR LOS PARTIDOS
    @GetMapping
    public List<Partido> listarPartidos() {
        return partidoRepository.findAll();
    }

    //API POST PARA CREAR UN NUEVO PARTIDO
    @PostMapping
    public Partido crearPartido(@RequestBody Partido partido) {
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
}

