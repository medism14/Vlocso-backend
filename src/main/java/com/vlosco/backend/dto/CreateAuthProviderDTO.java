package com.vlosco.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO pour la création d'un fournisseur d'authentification")
public class CreateAuthProviderDTO {
    
    @Schema(description = "Identifiant du fournisseur de compte", example = "12345", required = true)
    private String accountProviderId;

    @Schema(description = "Identifiant du fournisseur", example = "1", required = true)
    private Long providerId;

    @Schema(description = "Identifiant de l'utilisateur", example = "100", required = true)
    private Long userId;

    @Schema(description = "Obtenir l'identifiant du fournisseur de compte")
    public String getAccountProviderId() {
        return accountProviderId;
    }

    @Schema(description = "Définir l'identifiant du fournisseur de compte")
    public void setAccountProviderId(String accountProviderId) {
        this.accountProviderId = accountProviderId;
    }

    @Schema(description = "Obtenir l'identifiant du fournisseur")
    public Long getProviderId() {
        return providerId;
    }

    @Schema(description = "Définir l'identifiant du fournisseur")
    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    @Schema(description = "Obtenir l'identifiant de l'utilisateur")
    public Long getUserId() {
        return userId;
    }

    @Schema(description = "Définir l'identifiant de l'utilisateur")
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
