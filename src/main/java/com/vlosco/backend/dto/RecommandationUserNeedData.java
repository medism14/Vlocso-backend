package com.vlosco.backend.dto;

import java.util.List;

import com.vlosco.backend.model.Annonce;

public class RecommandationUserNeedData {
    private Long userId;
    private List<Annonce> annonces;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Annonce> getAnnonces() {
        return annonces;
    }

    public void setAnnonces(List<Annonce> annonces) {
        this.annonces = annonces;
    }

}
