package com.vlosco.backend.dto;

public class AnnonceSearchDTO {
    private String searchText;
    private String[] excludedIds;

    // Getters
    public String getSearchText() {
        return searchText;
    }

    public String[] getExcludedIds() {
        return excludedIds != null ? excludedIds : new String[0];
    }

    // Setters
    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public void setExcludedIds(String[] excludedIds) {
        this.excludedIds = excludedIds;
    }
} 