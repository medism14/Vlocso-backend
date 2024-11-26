package com.vlosco.backend.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Rôles d'utilisateur dans le système")
public enum UserRole {
    @Schema(description = "Rôle d'utilisateur standard")
    USER,
    
    @Schema(description = "Rôle d'administrateur avec des privilèges élevés")
    ADMIN
}
