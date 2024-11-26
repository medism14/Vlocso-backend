package com.vlosco.backend.dto;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Détails de l'annonce")
public class AnnonceDetailsDTO {
    
    @Schema(description = "Identifiant de l'annonce", example = "1")
    private Long id;

    @Schema(description = "Titre de l'annonce", example = "Vente d'une voiture")
    private String title;

    @Schema(description = "Prix de l'annonce", example = "15000.0")
    private Double price;

    @Schema(description = "Quantité d'articles", example = "1")
    private Integer quantity;

    @Schema(description = "Type de transaction (vente ou achat)", example = "vente")
    private String transaction; 

    @Schema(description = "Ville de l'annonce", example = "Paris")
    private String city; 

    @Schema(description = "Numéro de téléphone de contact", example = "+33123456789")
    private String phoneNumber; 

    @Schema(description = "Liste des URLs des images associées à l'annonce", example = "[\"http://example.com/image1.jpg\", \"http://example.com/image2.jpg\"]")
    private List<String> imageUrls; 

    @Schema(description = "Catégorie de l'annonce", example = "Véhicules")
    private String category; 

    @Schema(description = "Localisation de l'annonce", example = "Centre-ville")
    private String location; 

    @Schema(description = "Identifiant de l'utilisateur", example = "1")
    private Long userId; 

    public AnnonceDetailsDTO() {}

    public AnnonceDetailsDTO(Long id, String title, Double price, Integer quantity, String transaction, String city, String phoneNumber, List<String> imageUrls, String category, String location, Long userId) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
        this.transaction = transaction;
        this.city = city;
        this.phoneNumber = phoneNumber;
        this.imageUrls = imageUrls;
        this.category = category;
        this.location = location;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
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

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
}