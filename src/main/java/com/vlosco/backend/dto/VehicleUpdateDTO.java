package com.vlosco.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO pour la mise à jour des informations d'un véhicule")
public class VehicleUpdateDTO {
    
    @Schema(description = "Marque du véhicule", example = "Toyota")
    private String mark;

    @Schema(description = "Modèle du véhicule", example = "Corolla")
    private String model;

    @Schema(description = "Année de fabrication du véhicule", example = "2020")
    private Integer year;

    @Schema(description = "Type de boîte de vitesses", example = "Manuelle")
    private String gearbox;

    @Schema(description = "Type de véhicule", example = "Berline")
    private String type;

    @Schema(description = "Indique si le véhicule a la climatisation", example = "Oui")
    private String climatisation;

    @Schema(description = "Type de carburant", example = "Essence")
    private String fuelType;

    @Schema(description = "État du véhicule", example = "Neuf")
    private String condition;

    @Schema(description = "Compteur kilométrique", example = "15000")
    private String klm_counter;

    @Schema(description = "Description du véhicule", example = "Véhicule en excellent état")
    private String description;

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getGearbox() {
        return gearbox;
    }

    public void setGearbox(String gearbox) {
        this.gearbox = gearbox;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getClimatisation() {
        return climatisation;
    }

    public void setClimatisation(String climatisation) {
        this.climatisation = climatisation;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getKlm_counter() {
        return klm_counter;
    }

    public void setKlm_counter(String klm_counter) {
        this.klm_counter = klm_counter;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
