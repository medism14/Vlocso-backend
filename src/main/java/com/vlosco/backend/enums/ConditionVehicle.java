package com.vlosco.backend.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Conditions des véhicules disponibles")
public enum ConditionVehicle {
    @Schema(description = "Véhicule neuf, jamais utilisé")
    NEUF,
    
    @Schema(description = "Véhicule d'occasion, utilisé auparavant")
    OCCASION,
    
    @Schema(description = "Véhicule légèrement endommagé, nécessitant peu de réparations")
    PEU_ENDOMMAGE
}
