package com.vlosco.backend.dto;

import com.vlosco.backend.model.Annonce;
import com.vlosco.backend.model.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO représentant une annonce avec l'utilisateur associé")
public class AnnonceWithUserDTO {
    
    @Schema(description = "L'annonce associée", 
           example = "{ \"id\": 1, \"title\": \"Annonce Exemple\", \"description\": \"Description de l'annonce\" }")
    private Annonce annonce;
    
    @Schema(description = "L'utilisateur associé à l'annonce", 
           example = "{ \"id\": 1, \"username\": \"utilisateurExemple\", \"email\": \"utilisateur@example.com\" }")
    private User user; // ou un autre DTO pour l'utilisateur

    public AnnonceWithUserDTO(Annonce annonce, User user) {
        this.annonce = annonce;
        this.user = user;
    }

    @Schema(description = "Récupère l'annonce")
    public Annonce getAnnonce() {
        return annonce;
    }

    @Schema(description = "Définit l'annonce")
    public void setAnnonce(Annonce annonce) {
        this.annonce = annonce;
    }

    @Schema(description = "Récupère l'utilisateur")
    public User getUser() {
        return user;
    }

    @Schema(description = "Définit l'utilisateur")
    public void setUser(User user) {
        this.user = user;
    }
}
