package com.vlosco.backend.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO pour l'enregistrement d'un utilisateur avec un fournisseur")
public class UserRegistrationProviderDTO {
    
    @NotNull
    @Schema(description = "Nom du fournisseur", example = "GOOGLE", required = true)
    private ProviderNames providerName;

    @NotNull
    @Schema(description = "Identifiant du fournisseur de compte", example = "12345", required = true)
    private String accountProviderId;

    @NotNull
    @Schema(description = "Adresse email de l'utilisateur", example = "utilisateur@example.com", required = true)
    private String email;

    @NotNull
    @Schema(description = "Prénom de l'utilisateur", example = "Jean", required = true)
    private String firstName;

    @NotNull
    @Schema(description = "Nom de famille de l'utilisateur", example = "Dupont", required = true)
    private String lastName;

    @NotNull
    @Schema(description = "Date de naissance de l'utilisateur", example = "1990-01-01", required = true)
    private LocalDate birthDate;

    @NotNull
    @Schema(description = "Ville de résidence de l'utilisateur", example = "Paris", required = true)
    private String city;

    @NotNull
    @Schema(description = "Numéro de téléphone de l'utilisateur", example = "+33123456789", required = true)
    private String phoneNumber;

    @NotNull
    @Schema(description = "URL de l'image de l'utilisateur", example = "http://example.com/image.jpg", required = true)
    private String urlImageUser;

    @NotNull
    @Schema(description = "Indique si l'email a été vérifié", example = "true", required = true)
    private boolean emailVerified;

    @Schema(description = "Rôle de l'utilisateur", example = "ADMIN")
    private UserRole role;

    public ProviderNames getProviderName() {
        return providerName;
    }

    public void setProviderName(ProviderNames providerName) {
        this.providerName = providerName;
    }

    public String getAccountProviderId() {
        return accountProviderId;
    }

    public void setAccountProviderId(String accountProviderId) {
        this.accountProviderId = accountProviderId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
