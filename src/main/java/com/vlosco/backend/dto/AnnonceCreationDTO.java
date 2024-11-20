package com.vlosco.backend.dto;

import jakarta.validation.constraints.NotNull;

public class AnnonceCreationDTO {
    @NotNull
    private AnnonceDetailsCreationDTO annonce;

    @NotNull
    private VehicleCreationDTO vehicle;

    @NotNull
    private String[] images;

    public AnnonceDetailsCreationDTO getAnnonce() {
        return annonce;
    }

    public void setAnnonce(AnnonceDetailsCreationDTO annonce) {
        this.annonce = annonce;
    }

    public VehicleCreationDTO getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleCreationDTO vehicle) {
        this.vehicle = vehicle;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }
}
