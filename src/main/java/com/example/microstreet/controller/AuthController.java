package com.example.microstreet.controller;
import com.example.microstreet.model.Usuario;
import com.example.microstreet.repository.UsuarioRepository;
import com.example.microstreet.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200") // Permite que Angular se conecte
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {

        try {
            System.out.println("Intentando registrar a: " + usuario.getEmail());
            // DEBUG: Mira si la contraseña llega o llega null
            System.out.println("Password recibida: " + usuario.getPassword());

            if (usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
                return ResponseEntity.badRequest().body("La contraseña es obligatoria");
            }
            // Generar código
            String codigo = String.format("%06d", new Random().nextInt(999999));
            usuario.setCodigoVerificacion(codigo);
            usuario.setVerificado(false);
            // Guardar primero para ver si falla la DB
            usuarioRepository.save(usuario);
            System.out.println("Usuario guardado en DB");
            // Enviar correo
            emailService.enviarCodigoVerificacion(usuario.getEmail(), codigo);

            return ResponseEntity.ok("Registrado con éxito");
        } catch (Exception e) {
            e.printStackTrace(); // Esto imprimirá el error REAL en IntelliJ
            return ResponseEntity.status(500).body("Error interno: " + e.getMessage());
        }
    }
    @PostMapping("/verificar")
    public ResponseEntity<String> verificar(@RequestBody java.util.Map<String, String> datos) {
        String email = datos.get("email");
        String codigo = datos.get("codigo");

        // 1. Buscar al usuario por email y código
        return usuarioRepository.findByEmailAndCodigoVerificacion(email, codigo)
                .map(usuario -> {
                    // 2. Si lo encuentra, cambiamos el estado a verificado
                    usuario.setVerificado(true);
                    usuario.setCodigoVerificacion(null); // Limpiamos el código por seguridad
                    usuarioRepository.save(usuario);
                    return ResponseEntity.ok("Cuenta activada con éxito. Ya puedes iniciar sesión.");
                })
                // 3. Si no coincide el código o el email no existe
                .orElse(ResponseEntity.badRequest().body("Código incorrecto o correo no encontrado."));
    }
}