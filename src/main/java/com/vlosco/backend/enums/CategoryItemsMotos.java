package com.vlosco.backend.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Catégories d'articles pour motos")
public enum CategoryItemsMotos {
    @Schema(description = "Motos de type sport")
    SPORT,
    
    @Schema(description = "Motos de type cruiser")
    CRUISER,
    
    @Schema(description = "Motos de type touring")
    TOURING,
    
    @Schema(description = "Motos de type standard")
    STANDARD,
    
    @Schema(description = "Motos de type dual sport")
    DUAL_SPORT,
    
    @Schema(description = "Motos de type aventure")
    ADVENTURE,
    
    @Schema(description = "Motos de type chopper")
    CHOPPER,
    
    @Schema(description = "Motos de type café racer")
    CAFE_RACER,
    
    @Schema(description = "Motos électriques")
    ELECTRIC,
    
    @Schema(description = "Scooters")
    SCOOTER
}
