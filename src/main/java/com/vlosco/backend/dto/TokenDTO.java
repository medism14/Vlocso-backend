package com.vlosco.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tokens d'authentification")
public class TokenDTO {
    @Schema(description = "Token d'accès JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;
    
    @Schema(description = "Token de rafraîchissement", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    @Schema(description = "Obtient le token d'accès JWT")
    public String getAccessToken() {
        return accessToken;
    }

    @Schema(description = "Définit le token d'accès JWT")
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Schema(description = "Obtient le token de rafraîchissement")
    public String getRefreshToken() {
        return refreshToken;
    }

    @Schema(description = "Définit le token de rafraîchissement")
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}