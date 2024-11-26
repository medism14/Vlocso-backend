package com.vlosco.backend.dto;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO pour la création des détails d'une annonce.
 * Contient les informations nécessaires pour créer une annonce.
 */
@Schema(description = "Objet représentant les détails d'une annonce à créer.")
public class AnnonceDetailsCreationDTO {
    
    @NotNull
    @Schema(description = "Identifiant de l'utilisateur", example = "1", required = true)
    private Long userId;

    @NotNull
    @Schema(description = "Titre de l'annonce", example = "Vente d'une voiture", required = true)
    private String title;

    @NotNull
    @Schema(description = "Prix de l'annonce", example = "15000", required = true)
    private String price;

    @NotNull
    @Schema(description = "Type de transaction (vente ou achat)", example = "vente", required = true)
    private String transaction;

    @NotNull
    @Schema(description = "Quantité d'articles", example = "1", required = true)
    private Integer quantity;

    @NotNull
    @Schema(description = "Ville de l'annonce", example = "Paris", required = true)
    private String city;

    @Schema(description = "Numéro de téléphone de contact", example = "+33123456789")
    private String phoneNumber;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}