package com.vlosco.backend.dto;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO pour la création d'un véhicule")
public class VehicleCreationDTO {
    
    @NotNull
    @Schema(description = "Type de véhicule", example = "Voiture")
    private String type;

    @NotNull
    @Schema(description = "Marque du véhicule", example = "Toyota")
    private String mark;

    @NotNull
    @Schema(description = "Modèle du véhicule", example = "Corolla")
    private String model;

    @NotNull
    @Schema(description = "Année de fabrication", example = "2020")
    private Integer year;

    @NotNull
    @Schema(description = "Type de boîte de vitesses", example = "Manuelle")
    private String gearbox;

    @NotNull
    @Schema(description = "Indique si le véhicule a la climatisation", example = "Oui")
    private String climatisation;

    @NotNull
    @Schema(description = "État du véhicule", example = "Neuf")
    private String condition;

    @Schema(description = "Catégorie du véhicule", example = "Citadine")
    private String category;

    @Schema(description = "Couleur du véhicule", example = "Rouge")
    private String color;

    @NotNull
    @Schema(description = "Type de carburant", example = "Essence")
    private String fuelType;

    @Schema(description = "Compteur kilométrique", example = "15000")
    private Integer klmCounter;

    @Schema(description = "Description du véhicule", example = "Véhicule en excellent état")
    private String description;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    public String getClimatisation() {
        return climatisation;
    }

    public void setClimatisation(String climatisation) {
        this.climatisation = climatisation;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public Integer getKlmCounter() {
        return klmCounter;
    }

    public void setKlmCounter(Integer klmCounter) {
        this.klmCounter = klmCounter;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}