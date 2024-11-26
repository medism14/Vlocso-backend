package com.vlosco.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO pour le token de rafraîchissement")
public class RefreshTokenDTO {
    
    @Schema(description = "Le token de rafraîchissement", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    @Schema(description = "Obtenir le token de rafraîchissement")
    public String getRefreshToken() {
        return refreshToken;
    }

    @Schema(description = "Définir le token de rafraîchissement")
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
