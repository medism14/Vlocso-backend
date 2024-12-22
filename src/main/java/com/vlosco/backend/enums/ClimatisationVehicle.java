package com.vlosco.backend.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "État de la climatisation des véhicules")
public enum ClimatisationVehicle {
    @Schema(description = "Climatisation en très bon état")
    TRES_BONNE_ETAT,
    
    @Schema(description = "Climatisation en bon état")
    BONNE_ETAT,
    
    @Schema(description = "Climatisation en état moyen")
    MOYENNE_ETAT,
    
    @Schema(description = "Climatisation en mauvaise état")
    MAUVAISE_ETAT,
    
    @Schema(description = "Climatisation non fonctionnelle")
    NON_FONCTIONNEL
}
