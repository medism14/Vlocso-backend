package com.vlosco.backend.dto;

public class SimilarAnnonceRequestDTO {
    private Long annonceId;
    private Integer nbAnnonces;

    public Long getAnnonceId() {
        return annonceId;
    }

    public void setAnnonceId(Long annonceId) {
        this.annonceId = annonceId;
    }

    public Integer getNbAnnonces() {
        return nbAnnonces;
    }

    public void setNbAnnonces(Integer nbAnnonces) {
        this.nbAnnonces = nbAnnonces;
    }
} 