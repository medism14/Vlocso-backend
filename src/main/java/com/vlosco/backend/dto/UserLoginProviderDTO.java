package com.vlosco.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO pour la connexion de l'utilisateur avec le fournisseur")
public class UserLoginProviderDTO {
    
    @Schema(description = "Adresse email de l'utilisateur", example = "utilisateur@example.com", required = true)
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
