package com.vlosco.backend.dto;

import jakarta.validation.constraints.NotNull;

public class VehicleCreationDTO {
    @NotNull
    private String type;

    @NotNull
    private String mark;

    @NotNull
    private String model;

    @NotNull
    private Integer year;

    @NotNull
    private String gearbox;

    @NotNull
    private String climatisation;

    @NotNull
    private String condition;

    @NotNull
    private String fuelType;

    private String klm_counter;

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