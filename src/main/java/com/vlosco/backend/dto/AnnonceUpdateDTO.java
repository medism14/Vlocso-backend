package com.vlosco.backend.dto;

public class AnnonceUpdateDTO {
    private AnnonceDetailsUpdateDTO annonce;
    private VehicleUpdateDTO vehicle;
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