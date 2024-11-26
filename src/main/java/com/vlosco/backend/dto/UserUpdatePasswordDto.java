package com.vlosco.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO pour la mise à jour du mot de passe de l'utilisateur")
public class UserUpdatePasswordDto {
    
    @Schema(description = "Identifiant unique de l'utilisateur", example = "1")
    private Long id;
    
    @Schema(description = "Nouveau mot de passe de l'utilisateur", example = "MonNouveauMotDePasse123!")
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
