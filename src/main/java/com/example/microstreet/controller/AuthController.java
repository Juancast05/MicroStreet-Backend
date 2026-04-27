package com.example.microstreet.controller; // <-- Ajusta a tu paquete

import com.example.microstreet.model.Usuario; // <-- Ajusta a tu modelo
import com.example.microstreet.repository.UsuarioRepository; // <-- Ajusta a tu repo
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/registro")
    public ResponseEntity<String> registrar(@RequestBody Usuario usuario) {
        try {
            // Generar código de 6 dígitos
            String codigo = String.valueOf((int)(Math.random() * 900000) + 100000);
            usuario.setCodigoVerificacion(codigo);
            usuario.setVerificado(false);

            usuarioRepository.save(usuario);
            enviarEmail(usuario.getEmail(), codigo);

            return ResponseEntity.ok("Usuario registrado. Revisa tu correo.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al registrar: " + e.getMessage());
        }
    }

    @PostMapping("/verificar")
    public ResponseEntity<String> verificar(@RequestBody Map<String, String> datos) {
        String email = datos.get("email");
        String codigo = datos.get("codigo");

        Optional<Usuario> userOpt = usuarioRepository.findByEmail(email);
        if (userOpt.isPresent() && userOpt.get().getCodigoVerificacion().equals(codigo)) {
            Usuario user = userOpt.get();
            user.setVerificado(true);
            user.setCodigoVerificacion(null);
            usuarioRepository.save(user);
            return ResponseEntity.ok("Cuenta activada con éxito");
        }
        return ResponseEntity.badRequest().body("Código incorrecto o usuario no encontrado");
    }

    private void enviarEmail(String destinatario, String codigo) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinatario);
        message.setSubject("Código de Verificación - MicroStreet");
        message.setText("Tu código para activar tu cuenta en MicroStreet es: " + codigo);
        mailSender.send(message);
    }
}