package com.vlosco.backend.dto;

import java.util.List;


public class AnnonceDetailsDTO {
    private Long id;
    private String title;
    private Double price;
    private Integer quantity;
    private String transaction; 
    private String city; 
    private String phoneNumber; 
    private List<String> imageUrls; 
    private String category; 
    private String location; 
    private Long userId; 

    
    public AnnonceDetailsDTO() {}


    public AnnonceDetailsDTO(Long id, String title, String description, List<String> imageUrls, String category, String location, Long userId) {
        this.id = id;
        this.title = title;
        this.imageUrls = imageUrls;
        this.category = category;
        this.location = location;
        this.userId = userId;
    }

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
}