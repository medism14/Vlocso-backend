package com.vlosco.backend.dto;

import com.vlosco.backend.model.Annonce;
import com.vlosco.backend.model.User;

public class AnnonceWithUserDTO {
    private Annonce annonce;
    private User user; // ou un autre DTO pour l'utilisateur

    public AnnonceWithUserDTO(Annonce annonce, User user) {
        this.annonce = annonce;
        this.user = user;
    }

    public Annonce getAnnonce() {
        return annonce;
    }

    public void setAnnonce(Annonce annonce) {
        this.annonce = annonce;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
