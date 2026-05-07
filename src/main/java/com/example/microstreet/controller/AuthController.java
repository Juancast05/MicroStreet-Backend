package com.example.microstreet.controller;
import com.example.microstreet.model.Usuario;
import com.example.microstreet.repository.UsuarioRepository;
import com.example.microstreet.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.microstreet.dto.LoginRequest;
import java.util.Optional;
import java.util.Random;
import java.util.Map;

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
            if (usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
                return ResponseEntity.badRequest().body("La contraseña es obligatoria");
            }

            // Generar código de 6 dígitos
            String codigo = String.format("%06d", new Random().nextInt(999999));
            usuario.setCodigoVerificacion(codigo);
            usuario.setVerificado(false); // Por defecto no está verificado

            usuarioRepository.save(usuario);

            // Enviar correo electrónico
            emailService.enviarCodigoVerificacion(usuario.getEmail(), codigo);

            return ResponseEntity.ok("Registrado con éxito. Revisa tu correo.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno: " + e.getMessage());
        }
    }

    @PostMapping("/verificar")
    public ResponseEntity<String> verificar(@RequestBody Map<String, String> datos) {
        String email = datos.get("email");
        String codigo = datos.get("codigo");

        return usuarioRepository.findByEmailAndCodigoVerificacion(email, codigo)
                .map(usuario -> {
                    usuario.setVerificado(true);
                    usuario.setCodigoVerificacion(null); // Limpiamos el código
                    usuarioRepository.save(usuario);
                    return ResponseEntity.ok("Cuenta activada con éxito. Ya puedes iniciar sesión.");
                })
                .orElse(ResponseEntity.badRequest().body("Código incorrecto o correo no encontrado."));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // 1. Verificación de llegada de datos
        System.out.println(" Intentando login para: " + loginRequest.getEmail());

        // 2. Buscar usuario
        return usuarioRepository.findByEmail(loginRequest.getEmail())
                .map(usuario -> {
                    // 3. Verificar si está activado
                    System.out.println(" Usuario encontrado. Verificado: " + usuario.isVerificado());

                    if (!usuario.isVerificado()) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("Debes activar tu cuenta primero");
                    }

                    // 4. Verificar contraseña (Texto plano por ahora)
                    if (usuario.getPassword().equals(loginRequest.getPassword())) {
                        System.out.println(" LOGIN EXITOSO");
                        return ResponseEntity.ok("Login exitoso");
                    } else {
                        System.out.println(" CONTRASEÑA INCORRECTA");
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body("Contraseña incorrecta");
                    }
                })
                .orElseGet(() -> {
                    System.out.println(" USUARIO NO ENCONTRADO");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("El correo no está registrado");
                });
    }

}