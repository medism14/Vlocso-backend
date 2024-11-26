package com.vlosco.backend.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Types d'utilisateurs disponibles dans le système")
public enum UserType {
    
    @Schema(description = "Utilisateur régulier avec accès standard")
    REGULAR,
    
    @Schema(description = "Fournisseur avec accès étendu")
    PROVIDER
}
