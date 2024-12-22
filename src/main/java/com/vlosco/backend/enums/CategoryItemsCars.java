package com.vlosco.backend.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Catégories de véhicules disponibles")
public enum CategoryItemsCars {
    @Schema(description = "Véhicule utilitaire sport")
    SUV,
    
    @Schema(description = "Véhicule de sport")
    SPORT,
    
    @Schema(description = "Véhicule de type berline")
    BERLINE,
    
    @Schema(description = "Véhicule de type coupé")
    COUPE,
    
    @Schema(description = "Véhicule de type hatchback")
    HATCHBACK,
    
    @Schema(description = "Véhicule de type break")
    BREAK,
    
    @Schema(description = "Véhicule de type pick-up")
    PICK_UP,
    
    @Schema(description = "Véhicule de type monospace")
    MONOSPACE,
    
    @Schema(description = "Véhicule de type crossover")
    CROSSOVER,
    
    @Schema(description = "Véhicule de type roadster")
    ROADSTER,
    
    @Schema(description = "Véhicule utilitaire")
    UTILITAIRE
}
