package com.vlosco.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO pour la création d'un message")
public class MessageCreationDTO {
    
    @Schema(description = "Le contenu du message", example = "Bonjour, comment ça va?", required = true)
    private String content;

    @Schema(description = "L'identifiant de la conversation", example = "123", required = true)
    private Long conversationId;

    @Schema(description = "Obtenir le contenu du message")
    public String getContent() {
        return content;
    }

    @Schema(description = "Définir le contenu du message")
    public void setContent(String content) {
        this.content = content;
    }

    @Schema(description = "Obtenir l'identifiant de la conversation")
    public Long getConversationId() {
        return conversationId;
    }

    @Schema(description = "Définir l'identifiant de la conversation")
    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }
}
