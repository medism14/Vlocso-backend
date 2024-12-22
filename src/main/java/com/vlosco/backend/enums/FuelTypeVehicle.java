package com.vlosco.backend.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Types de carburant disponibles pour les véhicules")
public enum FuelTypeVehicle {
    @Schema(description = "Carburant à base d'essence")
    ESSENCE,
    
    @Schema(description = "Carburant à base de diesel")
    DIESEL,
    
    @Schema(description = "Véhicule fonctionnant à l'électricité")
    ELECTRIQUE,
    
    @Schema(description = "Véhicule hybride combinant essence et électricité")
    HYBRIDE
}
