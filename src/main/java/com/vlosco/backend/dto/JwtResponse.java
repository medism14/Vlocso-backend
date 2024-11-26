package com.vlosco.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Réponse JWT contenant le token d'authentification")
public class JwtResponse {
    
    @Schema(description = "Le token JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", required = true)
    private String token;

    public JwtResponse(@Schema(description = "Le token JWT à initialiser", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...") String token) {
        this.token = token;
    }

    @Schema(description = "Obtenir le token JWT")
    public String getToken() {
        return token;
    }

    @Schema(description = "Définir le token JWT")
    public void setToken(@Schema(description = "Le token JWT à définir", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...") String token) {
        this.token = token;
    }
}
