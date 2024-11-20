package com.vlosco.backend.dto;

public class UpdateAuthProviderDTO {
    private String accountProviderId;
    private Long providerId;
    private Long userId;

    public String getAccountProviderId() {
        return accountProviderId;
    }

    public void setAccountProviderId(String accountProviderId) {
        this.accountProviderId = accountProviderId;
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
