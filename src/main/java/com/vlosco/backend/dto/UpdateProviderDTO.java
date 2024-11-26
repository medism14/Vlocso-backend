package com.vlosco.backend.dto;

import com.vlosco.backend.enums.ProviderNames;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO pour mettre à jour le fournisseur")
public class UpdateProviderDTO {
    
    @Schema(description = "Nom du fournisseur", example = "GOOGLE")
    private ProviderNames providerName;

    @Schema(description = "Obtenir le nom du fournisseur")
    public ProviderNames getProviderName() {
        return providerName;
    }

    @Schema(description = "Définir le nom du fournisseur")
    public void setProviderName(ProviderNames providerName) {
        this.providerName = providerName;
    }
}
