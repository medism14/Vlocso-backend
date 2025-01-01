package com.vlosco.backend.dto;

public class AnnonceSearchDTO {
    private String searchText;
    private String[] excludedIds;
    private Integer nbAnnonces; 

    // Getters
    public String getSearchText() {
        return searchText;
    }

    public String[] getExcludedIds() {
        return excludedIds != null ? excludedIds : new String[0];
    }

    public Integer getNbAnnonces() { // Getter pour nbAnnonces
        return nbAnnonces;
    }

    // Setters
    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public void setExcludedIds(String[] excludedIds) {
        this.excludedIds = excludedIds;
    }

    public void setNbAnnonces(Integer nbAnnonces) { // Setter pour nbAnnonces
        this.nbAnnonces = nbAnnonces;
    }
} 