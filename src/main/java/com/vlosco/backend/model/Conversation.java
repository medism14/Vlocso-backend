package com.vlosco.backend.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "conversations")
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;

    @ManyToOne
    @JoinColumn(name = "annonce_id", nullable = false)
    @JsonManagedReference
    private Annonce annonce;

    @ManyToOne
    @JoinColumn(name = "buyer_id", nullable = false)
    @JsonBackReference
    private User buyer;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_active_for_vendor", nullable = false)
    private boolean isActiveForVendor;

    @Column(name = "is_active_for_buyer", nullable = false)
    private boolean isActiveForBuyer;

    @OneToMany(mappedBy = "conversation")
    @JsonManagedReference
    private List<Message> messages;


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public Annonce getAnnonce() {
        return annonce;
    }

    public void setAnnonce(Annonce annonce) {
        this.annonce = annonce;
    }

    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActiveForVendor() {
        return isActiveForVendor;
    }

    public void setActiveForVendor(boolean isActiveForVendor) {
        this.isActiveForVendor = isActiveForVendor;
    }

    public boolean isActiveForBuyer() {
        return isActiveForBuyer;
    }

    public void setActiveForBuyer(boolean isActiveForBuyer) {
        this.isActiveForBuyer = isActiveForBuyer;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
