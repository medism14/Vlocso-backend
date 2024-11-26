package com.vlosco.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO pour mettre à jour le fournisseur d'authentification")
public class UpdateAuthProviderDTO {
    
    @Schema(description = "Identifiant du fournisseur de compte", example = "12345")
    private String accountProviderId;

    @Schema(description = "Identifiant du fournisseur", example = "1")
    private Long providerId;

    @Schema(description = "Identifiant de l'utilisateur", example = "1001")
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
