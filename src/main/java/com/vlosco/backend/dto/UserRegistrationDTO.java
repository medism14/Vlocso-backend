package com.vlosco.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "DTO pour l'enregistrement d'un utilisateur")
public class UserRegistrationDTO {
    
    @Schema(description = "Adresse email de l'utilisateur", example = "utilisateur@example.com", required = true)
    private String email;

    @Schema(description = "Mot de passe de l'utilisateur", example = "password123", required = true)
    private String password;

    @Schema(description = "Prénom de l'utilisateur", example = "Jean", required = true)
    private String firstName;

    @Schema(description = "Nom de famille de l'utilisateur", example = "Dupont", required = true)
    private String lastName;

    @Schema(description = "Date de naissance de l'utilisateur", example = "1990-01-01")
    private LocalDate birthDate;

    @Schema(description = "Ville de résidence de l'utilisateur", example = "Paris")
    private String city;

    @Schema(description = "Numéro de téléphone de l'utilisateur", example = "+33123456789")
    private String phoneNumber;

    @Schema(description = "URL de l'image de l'utilisateur", example = "http://example.com/image.jpg")
    private String urlImageUser;

    @Schema(description = "Indique si l'email a été vérifié", example = "true")
    private boolean emailVerified;

    @Schema(description = "Type d'utilisateur", example = "USER")
    private UserType type;

    @Schema(description = "Rôle de l'utilisateur", example = "ADMIN")
    private UserRole role;

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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
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

    public String getUrlImageUser() {
        return urlImageUser;
    }

    public void setUrlImageUser(String urlImageUser) {
        this.urlImageUser = urlImageUser;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
