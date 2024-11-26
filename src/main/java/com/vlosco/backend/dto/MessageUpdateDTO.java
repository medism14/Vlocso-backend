package com.vlosco.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO pour la mise à jour d'un message")
public class MessageUpdateDTO {
    
    @Schema(description = "Nouveau contenu du message", required = true, example = "Ceci est le nouveau contenu du message.")
    private String newContent;

    public String getNewContent() {
        return newContent;
    }

    public void setNewContent(String newContent) {
        this.newContent = newContent;
    }
}
