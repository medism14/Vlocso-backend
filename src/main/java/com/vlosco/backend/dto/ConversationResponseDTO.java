package com.vlosco.backend.dto;

import com.vlosco.backend.model.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.time.LocalDateTime;

@Schema(description = "DTO pour la réponse d'une conversation")
public class ConversationResponseDTO {
    
    @Schema(description = "Identifiant de la conversation")
    private Long conversationId;

    @Schema(description = "Annonce associée à la conversation")
    private AnnonceWithUserDTO annoncewithUser;

    @Schema(description = "Acheteur participant à la conversation")
    private User buyer;

    @Schema(description = "Vendeur participant à la conversation")
    private User vendor;

    @Schema(description = "Liste des messages dans la conversation")
    private List<MessageResponseDTO> messages;

    @Schema(description = "Indique si la conversation est active pour le vendeur")
    private boolean isActiveForVendor;

    @Schema(description = "Indique si la conversation est active pour l'acheteur")
    private boolean isActiveForBuyer;

    @Schema(description = "Date de création de la conversation")
    private LocalDateTime createdAt;

    // Getters et Setters
    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public AnnonceWithUserDTO getAnnonceWithUserDTO() {
        return annoncewithUser;
    }

    public void setAnnonceWithUserDto(AnnonceWithUserDTO annoncewithUser) {
        this.annoncewithUser = annoncewithUser;
    }

    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public User getVendor() {
        return vendor;
    }

    public void setVendor(User vendor) {
        this.vendor = vendor;
    }

    public List<MessageResponseDTO> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageResponseDTO> messages) {
        this.messages = messages;
    }

    public boolean isActiveForVendor() {
        return isActiveForVendor;
    }

    public void setActiveForVendor(boolean isActiveForVendor) {
        this.isActiveForVendor = isActiveForVendor;
    }

    public boolean isActiveForBuyer() {
        return isActiveForBuyer;
    }

    public void setActiveForBuyer(boolean isActiveForBuyer) {
        this.isActiveForBuyer = isActiveForBuyer;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
