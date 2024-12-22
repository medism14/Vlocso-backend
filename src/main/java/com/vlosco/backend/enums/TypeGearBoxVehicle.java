package com.vlosco.backend.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Types de boîte de vitesses disponibles pour les véhicules")
public enum TypeGearBoxVehicle {
    @Schema(description = "Boîte de vitesses manuelle")
    MANUEL,
    
    @Schema(description = "Boîte de vitesses automatique")
    AUTOMATIQUE,
    
    @Schema(description = "Boîte de vitesses semi-automatique")
    SEMI_AUTOMATIQUE
}
