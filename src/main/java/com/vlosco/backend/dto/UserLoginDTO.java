package com.vlosco.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO pour la connexion de l'utilisateur")
public class UserLoginDTO {
    
    @Schema(description = "Adresse email de l'utilisateur", example = "user@example.com", required = true)
    private String email;

    @Schema(description = "Mot de passe de l'utilisateur", example = "password123", required = true)
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
