package com.vlosco.backend.dto;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO pour la création d'une annonce.
 * Contient les détails de l'annonce, le véhicule associé et les images.
 */
@Schema(description = "Objet représentant les données nécessaires à la création d'une annonce")
public class AnnonceCreationDTO {
    
    @NotNull
    @Schema(description = "Détails de l'annonce", required = true, example = "{\"title\": \"Vente d'une voiture\", \"description\": \"Une belle voiture en excellent état.\"}")
    private AnnonceDetailsCreationDTO annonce;

    @NotNull
    @Schema(description = "Détails du véhicule associé à l'annonce", required = true, example = "{\"make\": \"Toyota\", \"model\": \"Corolla\", \"year\": 2020}")
    private VehicleCreationDTO vehicle;

    @NotNull
    @Schema(description = "Tableau d'URLs des images associées à l'annonce", required = true, example = "[\"http://example.com/image1.jpg\", \"http://example.com/image2.jpg\"]")
    private String[] images;

    @Schema(description = "Obtenir les détails de l'annonce")
    public AnnonceDetailsCreationDTO getAnnonce() {
        return annonce;
    }

    @Schema(description = "Définir les détails de l'annonce")
    public void setAnnonce(AnnonceDetailsCreationDTO annonce) {
        this.annonce = annonce;
    }

    @Schema(description = "Obtenir les détails du véhicule associé")
    public VehicleCreationDTO getVehicle() {
        return vehicle;
    }

    @Schema(description = "Définir les détails du véhicule associé")
    public void setVehicle(VehicleCreationDTO vehicle) {
        this.vehicle = vehicle;
    }

    @Schema(description = "Obtenir les URLs des images associées à l'annonce")
    public String[] getImages() {
        return images;
    }

    @Schema(description = "Définir les URLs des images associées à l'annonce")
    public void setImages(String[] images) {
        this.images = images;
    }
}
