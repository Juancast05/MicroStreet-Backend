package com.example.microstreet.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarCodigoVerificacion(String to, String codigo) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Código de Verificación - MicroStreet");
        message.setText("Tu código de seguridad es: " + codigo +
                "\n\nÚsalo para activar tu cuenta y empezar a armar partidos.");

        mailSender.send(message);
    }
}