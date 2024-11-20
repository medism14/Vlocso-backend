package com.vlosco.backend.dto;

import com.vlosco.backend.model.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Réponse d'authentification contenant les informations utilisateur et les tokens")
public class AuthResponse {
    @Schema(description = "Informations de l'utilisateur connecté")
    private User user;
    
    @Schema(description = "Tokens d'authentification (access token et refresh token)")
    private TokenDTO tokens;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TokenDTO getTokens() {
        return tokens;
    }

    public void setTokens(TokenDTO tokens) {
        this.tokens = tokens;
    }
}