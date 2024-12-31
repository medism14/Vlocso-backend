package com.vlosco.backend.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Statut du paiement premium")
public enum PaymentStatus {
    @Schema(description = "Paiement en attente")
    PENDING,
    
    @Schema(description = "Paiement confirmé")
    CONFIRMED,
    
    @Schema(description = "Paiement refusé")
    REJECTED,
    
    @Schema(description = "Paiement remboursé")
    REFUNDED,
    
    @Schema(description = "Paiement expiré")
    EXPIRED
} 