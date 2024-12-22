package com.vlosco.backend.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Types de véhicules disponibles")
public enum TypeVehicle {
    @Schema(description = "Type de véhicule : Voiture")
    VOITURE,
    
    @Schema(description = "Type de véhicule : Moto")
    MOTO
}
