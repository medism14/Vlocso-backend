package com.vlosco.backend.dto;

import com.vlosco.backend.model.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Réponse d'authentification contenant les informations utilisateur et les tokens")
public class AuthResponse {
    
    @Schema(description = "Informations de l'utilisateur connecté", 
           example = "{ \"id\": 1, \"username\": \"utilisateurExemple\", \"email\": \"utilisateur@example.com\" }")
    private User user;
    
    @Schema(description = "Tokens d'authentification (access token et refresh token)", 
           example = "{ \"accessToken\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsImlhdCI6MTYyMjQ5MjAwMCwiZXhwIjoxNjIyNTc4MDAwfQ.eyJzdWIiOiIxIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNjIyNDkyMDAwfQ.5c8e1c1e1e1e1e1e1e1e1e1e1e1e1e1e1e1e1e1e1e1e1e1e1e1e1e1e1e1e1e1\", \"refreshToken\": \"dGVzdF90b2tlbg==\" }")
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