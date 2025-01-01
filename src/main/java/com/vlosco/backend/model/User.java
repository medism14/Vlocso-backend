package com.vlosco.backend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.vlosco.backend.enums.UserRole;
import com.vlosco.backend.enums.UserType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = true)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private UserType type;

    @Column(name = "phone_number", nullable = true)
    private String phoneNumber;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "url_image_user", nullable = true)
    private String urlImageUser;

    @Column(name = "unread_count_notifications", nullable = false)
    private Integer unreadCountNotifications;

    @Column(name = "last_login", nullable = false)
    private LocalDateTime lastLogin;

    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified;

    @Column(name = "email_verification_token", nullable = true)
    private String emailVerificationToken;

    @Column(name = "email_verification_token_expiration", nullable = true)
    private LocalDateTime emailVerificationTokenExpiration;

    @Column(name = "password_verification_token", nullable = true)
    private String passwordVerificationToken;

    @Column(name = "password_verification_token_expiration", nullable = true)
    private LocalDateTime passwordVerificationTokenExpiration;

    @Column(name = "is_active", nullable = true)
    private Boolean isActive;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "vendor")
    @JsonManagedReference("user-annonces")
    private List<Annonce> annonces;

    @OneToMany(mappedBy = "user")
    @JsonBackReference
    private List<Interaction> interactions;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference("user-notifications")
    private List<Notification> notifications;

    @OneToMany(mappedBy = "buyer")
    @JsonBackReference
    private List<Conversation> conversations;

    @OneToMany(mappedBy = "sender")
    @JsonBackReference
    private List<Message> messagesSent;

    @OneToMany(mappedBy = "receiver")
    @JsonBackReference
    private List<Message> messagesReceived;

    @OneToMany(mappedBy = "user")
    @JsonBackReference
    private List<AuthProvider> authProviders;

    @OneToMany(mappedBy = "user")
    @JsonBackReference
    private List<RefreshTokensBlacklist> refreshTokensBlacklist;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isActive = true;
        this.country = "France";
        this.unreadCountNotifications = 0;
        this.lastLogin = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getUrlImageUser() {
        return urlImageUser;
    }

    public void setUrlImageUser(String urlImageUser) {
        this.urlImageUser = urlImageUser;
    }

    public Integer getUnreadCountNotifications() {
        return unreadCountNotifications;
    }

    public void setUnreadCountNotifications(Integer unreadCountNotifications) {
        this.unreadCountNotifications = unreadCountNotifications;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getEmailVerificationToken() {
        return emailVerificationToken;
    }

    public void setEmailVerificationToken(String emailVerificationToken) {
        this.emailVerificationToken = emailVerificationToken;
    }

    public LocalDateTime getEmailVerificationTokenExpiration() {
        return emailVerificationTokenExpiration;
    }

    public void setEmailVerificationTokenExpiration(LocalDateTime emailVerificationTokenExpiration) {
        this.emailVerificationTokenExpiration = emailVerificationTokenExpiration;
    }

    public String getPasswordVerificationToken() {
        return passwordVerificationToken;
    }

    public void setPasswordVerificationToken(String passwordVerificationToken) {
        this.passwordVerificationToken = passwordVerificationToken;
    }

    public LocalDateTime getPasswordVerificationTokenExpiration() {
        return passwordVerificationTokenExpiration;
    }

    public void setPasswordVerificationTokenExpiration(LocalDateTime passwordVerificationTokenExpiration) {
        this.passwordVerificationTokenExpiration = passwordVerificationTokenExpiration;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<Annonce> getAnnonces() {
        return annonces;
    }

    public void setAnnonces(List<Annonce> annonces) {
        this.annonces = annonces;
    }

    public List<Interaction> getInteractions() {
        return interactions;
    }

    public void setInteractions(List<Interaction> interactions) {
        this.interactions = interactions;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public List<Conversation> getConversations() {
        return conversations;
    }

    public void setConversations(List<Conversation> conversations) {
        this.conversations = conversations;
    }

    public List<Message> getMessagesSent() {
        return messagesSent;
    }

    public void setMessagesSent(List<Message> messagesSent) {
        this.messagesSent = messagesSent;
    }

    public List<Message> getMessagesReceived() {
        return messagesReceived;
    }

    public void setMessagesReceived(List<Message> messagesReceived) {
        this.messagesReceived = messagesReceived;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<AuthProvider> getAuthProviders() {
        return authProviders;
    }

    public void setAuthProviders(List<AuthProvider> authProviders) {
        this.authProviders = authProviders;
    }

    public List<RefreshTokensBlacklist> getRefreshTokensBlacklist() {
        return refreshTokensBlacklist;
    }

    public void setRefreshTokensBlacklist(List<RefreshTokensBlacklist> refreshTokensBlacklist) {
        this.refreshTokensBlacklist = refreshTokensBlacklist;
    }
}