package com.vlosco.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO pour la création d'une conversation")
public class ConversationCreationDTO {

    @Schema(description = "Identifiant de l'annonce", example = "1", required = true)
    private Long annonceId;

    @Schema(description = "Identifiant de l'acheteur", example = "2", required = true)
    private Long buyerId;

    // Getters et Setters
    @Schema(description = "Obtenir l'identifiant de l'annonce")
    public Long getAnnonceId() {
        return annonceId;
    }

    @Schema(description = "Définir l'identifiant de l'annonce")
    public void setAnnonceId(Long annonceId) {
        this.annonceId = annonceId;
    }

    @Schema(description = "Obtenir l'identifiant de l'acheteur")
    public Long getBuyerId() {
        return buyerId;
    }

    @Schema(description = "Définir l'identifiant de l'acheteur")
    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }
}
