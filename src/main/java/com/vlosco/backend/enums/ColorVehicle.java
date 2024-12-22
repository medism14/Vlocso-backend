package com.vlosco.backend.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Couleurs de véhicules disponibles")
public enum ColorVehicle {
    @Schema(description = "Couleur rouge")
    ROUGE,
    
    @Schema(description = "Couleur bleue")
    BLEU,
    
    @Schema(description = "Couleur verte")
    VERT,
    
    @Schema(description = "Couleur noire")
    NOIR,
    
    @Schema(description = "Couleur blanche")
    BLANC,
    
    @Schema(description = "Couleur grise")
    GRIS,
    
    @Schema(description = "Couleur jaune")
    JAUNE,
    
    @Schema(description = "Couleur orange")
    ORANGE,
    
    @Schema(description = "Couleur violette")
    VIOLET,
    
    @Schema(description = "Couleur rose")
    ROSE
}
