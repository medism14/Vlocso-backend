package com.vlosco.backend.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "État de l'annonce")
public enum AnnonceState {
    @Schema(description = "Annonce active")
    ACTIVE,
    
    @Schema(description = "Annonce inactive")
    INACTIVE,
    
    @Schema(description = "Annonce vendue")
    SOLD,

    @Schema(description = "Annonce expirée")
    EXPIRED
}
