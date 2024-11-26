package com.vlosco.backend.dto;

import com.vlosco.backend.enums.AnnonceState;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO pour la mise à jour des détails d'une annonce")
public class AnnonceDetailsUpdateDTO {
    
    @Schema(description = "Titre de l'annonce", example = "Vente d'une voiture")
    private String title;

    @Schema(description = "Prix de l'annonce", example = "15000", required = true)
    private String price;

    @Schema(description = "Type de transaction (vente ou achat)", example = "vente", required = true)
    private String transaction;

    @Schema(description = "Quantité d'articles", example = "1", required = true)
    private Integer quantity;

    @Schema(description = "Ville de l'annonce", example = "Paris", required = true)
    private String city;

    @Schema(description = "Numéro de téléphone de contact", example = "+33123456789")
    private String phoneNumber;

    @Schema(description = "État de l'annonce", example = "ACTIVe")
    private AnnonceState annonceState;

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

    public AnnonceState getAnnonceState() {
        return annonceState;
    }

    public void setAnnonceState(AnnonceState annonceState) {
        this.annonceState = annonceState;
    }
}
