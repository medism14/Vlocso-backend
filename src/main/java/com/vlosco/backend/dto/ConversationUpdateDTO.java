package com.vlosco.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO pour la mise à jour d'une conversation")
public class ConversationUpdateDTO {
    
    @Schema(description = "Indique si la conversation est active pour le vendeur", example = "true", required = true)
    private boolean isActiveForVendor;

    @Schema(description = "Indique si la conversation est active pour l'acheteur", example = "false", required = true)
    private boolean isActiveForBuyer;

    @Schema(description = "Récupère l'état d'activité pour le vendeur", example = "true")
    public boolean isActiveForVendor() {
        return isActiveForVendor;
    }

    @Schema(description = "Définit l'état d'activité pour le vendeur", example = "true")
    public void setActiveForVendor(boolean isActiveForVendor) {
        this.isActiveForVendor = isActiveForVendor;
    }

    @Schema(description = "Récupère l'état d'activité pour l'acheteur", example = "false")
    public boolean isActiveForBuyer() {
        return isActiveForBuyer;
    }

    @Schema(description = "Définit l'état d'activité pour l'acheteur", example = "false")
    public void setActiveForBuyer(boolean isActiveForBuyer) {
        this.isActiveForBuyer = isActiveForBuyer;
    }
}
