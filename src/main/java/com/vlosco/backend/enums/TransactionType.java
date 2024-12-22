package com.vlosco.backend.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Types de transactions disponibles")
public enum TransactionType {
    @Schema(description = "Transaction de vente")
    VENTE,
    
    @Schema(description = "Transaction de location")
    LOCATION
}
