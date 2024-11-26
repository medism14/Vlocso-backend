package com.vlosco.backend.dto;

import com.vlosco.backend.enums.ProviderNames;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO pour la création d'un fournisseur")
public class CreateProviderDTO {
    
    @Schema(description = "Nom du fournisseur", required = true, example = "Fournisseur A")
    private ProviderNames providerName;

    @Schema(description = "Obtenir le nom du fournisseur", example = "Fournisseur A")
    public ProviderNames getProviderName() {
        return providerName;
    }

    @Schema(description = "Définir le nom du fournisseur", example = "Fournisseur B")
    public void setProviderName(ProviderNames providerName) {
        this.providerName = providerName;
    }
}
