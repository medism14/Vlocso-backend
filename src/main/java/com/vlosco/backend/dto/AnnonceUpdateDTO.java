package com.vlosco.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO pour la mise à jour d'une annonce")
public class AnnonceUpdateDTO {
    
    @Schema(description = "Détails de l'annonce", required = true, example = "{\"annonceId\": 1, \"title\": \"Vente d'une voiture\", \"price\": \"15000\", \"transaction\": \"vente\", \"quantity\": 1, \"city\": \"Paris\", \"phoneNumber\": \"+33123456789\"}")
    private AnnonceDetailsUpdateDTO annonce;

    @Schema(description = "Détails du véhicule associé à l'annonce", required = true, example = "{\"make\": \"Toyota\", \"model\": \"Corolla\", \"year\": 2020}")
    private VehicleUpdateDTO vehicle;

    @Schema(description = "Tableau d'URLs des images associées à l'annonce", example = "[\"http://example.com/image1.jpg\", \"http://example.com/image2.jpg\"]")
    private String[] images;

    public AnnonceDetailsUpdateDTO getAnnonce() {
        return annonce;
    }

    public void setAnnonce(AnnonceDetailsUpdateDTO annonce) {
        this.annonce = annonce;
    }

    public VehicleUpdateDTO getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleUpdateDTO vehicle) {
        this.vehicle = vehicle;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }
}