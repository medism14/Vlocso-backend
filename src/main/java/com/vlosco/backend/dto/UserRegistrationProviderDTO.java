package com.vlosco.backend.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public class UserRegistrationProviderDTO {
    @NotNull
    private ProviderNames providerName;

    @NotNull
    private String accountProviderId;

    @NotNull
    private String email;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private LocalDate birthDate;

    @NotNull
    private String city;

    @NotNull
    private String phoneNumber;

    @NotNull
    private String urlImageUser;

    @NotNull
    private boolean emailVerified;

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
