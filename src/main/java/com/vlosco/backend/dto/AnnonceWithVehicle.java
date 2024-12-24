package com.vlosco.backend.dto;

import com.vlosco.backend.model.Annonce;
import com.vlosco.backend.model.Vehicle;

public class AnnonceWithVehicle {
    private Annonce annonce;
    private Vehicle vehicle;

    public AnnonceWithVehicle(Annonce annonce, Vehicle vehicle) {
        this.annonce = annonce;
        this.vehicle = vehicle;
    }

    public Annonce getAnnonce() {
        return annonce;
    }

    public void setAnnonce(Annonce annonce) {
        this.annonce = annonce;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
}
