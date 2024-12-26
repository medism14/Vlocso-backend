package com.vlosco.backend.dto;

import java.util.List;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class AnnonceFilterDTO {
    @NotNull(message = "La liste d'IDs ne peut pas être null")
    private List<Long> annonceIds;
    
    @Min(value = 0, message = "Le prix minimum ne peut pas être négatif")
    private Double minPrice;
    
    @Min(value = 0, message = "Le prix maximum ne peut pas être négatif")
    private Double maxPrice;
    
    @Pattern(regexp = "^(date_asc|date_desc|price_asc|price_desc)$", 
            message = "Valeur de tri invalide")
    private String sortBy;
    
    @Pattern(regexp = "^(voiture|moto)$", 
            message = "Type de véhicule invalide")
    private String vehicleType;
    
    @Min(value = 0, message = "Le kilométrage minimum ne peut pas être négatif")
    private Integer kilometrageMin;
    
    @Min(value = 0, message = "Le kilométrage maximum ne peut pas être négatif")
    private Integer kilometrageMax;

    // Validation personnalisée
    @AssertTrue(message = "Le prix maximum doit être supérieur au prix minimum")
    private boolean isPriceRangeValid() {
        if (minPrice == null || maxPrice == null) return true;
        return maxPrice >= minPrice;
    }

    @AssertTrue(message = "Le kilométrage maximum doit être supérieur au kilométrage minimum")
    private boolean isKilometrageRangeValid() {
        if (kilometrageMin == null || kilometrageMax == null) return true;
        return kilometrageMax >= kilometrageMin;
    }

    // Getters et Setters
    public List<Long> getAnnonceIds() {
        return annonceIds;
    }

    public void setAnnonceIds(List<Long> annonceIds) {
        this.annonceIds = annonceIds;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Integer getKilometrageMin() {
        return kilometrageMin;
    }

    public void setKilometrageMin(Integer kilometrageMin) {
        this.kilometrageMin = kilometrageMin;
    }

    public Integer getKilometrageMax() {
        return kilometrageMax;
    }

    public void setKilometrageMax(Integer kilometrageMax) {
        this.kilometrageMax = kilometrageMax;
    }
} 