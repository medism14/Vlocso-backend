package com.vlosco.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Représente une réponse générique contenant des données et un message.")
public class ResponseDTO<T> {
    
    @Schema(description = "Les données de la réponse", example = "{\"key\": \"value\"}")
    private T data;

    @Schema(description = "Message associé à la réponse", example = "Opération réussie")
    private String message;

    public ResponseDTO() {
    }

    public ResponseDTO(T data, String message) {
        this.data = data;
        this.message = message;
    }

    public ResponseDTO(String message) {
        this.message = message;
    }

    @Schema(description = "Obtient les données de la réponse")
    public T getData() {
        return data;
    }

    @Schema(description = "Définit les données de la réponse")
    public void setData(T data) {
        this.data = data;
    }

    @Schema(description = "Obtient le message de la réponse")
    public String getMessage() {
        return message;
    }

    @Schema(description = "Définit le message de la réponse")
    public void setMessage(String message) {
        this.message = message;
    }
}