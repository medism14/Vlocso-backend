package com.vlosco.backend.dto;

public class PaymentPremiumDTO {
    private Long userId;
    private Long annonceId;
    private Double amount;
    private String paymentMethod;

    // Getters
    public Long getUserId() {
        return userId;
    }

    public Long getAnnonceId() {
        return annonceId;
    }

    public Double getAmount() {
        return amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    // Setters
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setAnnonceId(Long annonceId) {
        this.annonceId = annonceId;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
} 