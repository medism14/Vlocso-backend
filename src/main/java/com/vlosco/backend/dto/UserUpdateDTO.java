package com.vlosco.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO pour la mise à jour des informations utilisateur")
public class UserUpdateDTO {
    
    @Schema(description = "Prénom de l'utilisateur", example = "Jean")
    private String firstName;

    @Schema(description = "Nom de famille de l'utilisateur", example = "Dupont")
    private String lastName;

    @Schema(description = "Adresse email de l'utilisateur", example = "jean.dupont@example.com")
    private String email;

    @Schema(description = "Mot de passe de l'utilisateur", example = "password123")
    private String password;

    @Schema(description = "Numéro de téléphone de l'utilisateur", example = "+33123456789")
    private String phoneNumber;

    @Schema(description = "Pays de l'utilisateur", example = "France")
    private String country;

    @Schema(description = "Ville de l'utilisateur", example = "Paris")
    private String city;

    @Schema(description = "URL de l'image de l'utilisateur", example = "http://example.com/image.jpg")
    private String urlImageUser;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUrlImageUser() {
        return urlImageUser;
    }

    public void setUrlImageUser(String urlImageUser) {
        this.urlImageUser = urlImageUser;
    }
}
