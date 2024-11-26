package com.vlosco.backend.dto;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Représente une annonce premium.")
public class PremiumAnnonceDTO {
    @NotNull
    @Schema(description = "Identifiant unique de l'annonce", example = "1")
    private Long annonceId;

    @Schema(description = "Titre de l'annonce", example = "Vente d'une voiture")
    private String title;

    @Schema(description = "Numéro de téléphone de contact", example = "+33123456789")
    private String phoneNumber;

    @Schema(description = "Indique si l'annonce est premium", example = "true")
    private boolean premium; 

    @Schema(description = "Obtient l'identifiant de l'annonce", example = "1")
    public Long getAnnonceId() {
        return annonceId;
    }

    @Schema(description = "Définit l'identifiant de l'annonce", example = "1")
    public void setAnnonceId(Long annonceId) {
        this.annonceId = annonceId;
    }

    @Schema(description = "Obtient le titre de l'annonce", example = "Vente d'une voiture")
    public String getTitle() {
        return title;
    }

    @Schema(description = "Définit le titre de l'annonce", example = "Vente d'une voiture")
    public void setTitle(String title) {
        this.title = title;
    }

    @Schema(description = "Obtient le numéro de téléphone de contact", example = "+33123456789")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Schema(description = "Définit le numéro de téléphone de contact", example = "+33123456789")
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Schema(description = "Vérifie si l'annonce est premium", example = "true")
    public boolean getPremium() {
        return premium;
    }

    @Schema(description = "Définit si l'annonce est premium", example = "true")
    public void setPremium(boolean premium) {
        this.premium = premium;
    }
}
