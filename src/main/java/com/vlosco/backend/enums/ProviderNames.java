package com.vlosco.backend.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Noms des fournisseurs")
public enum ProviderNames {
    @Schema(description = "Fournisseur Google")
    GOOGLE,
    
    @Schema(description = "Fournisseur Facebook")
    FACEBOOK,
    
    @Schema(description = "Fournisseur Apple")
    APPLE
}