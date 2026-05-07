package com.example.microstreet.dto;

public class LoginRequest {
    private String email;
    private String password;

    // Constructor vacío obligatorio
    public LoginRequest() {}

    // Getters y Setters manuales para evitar fallos de compilación
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}