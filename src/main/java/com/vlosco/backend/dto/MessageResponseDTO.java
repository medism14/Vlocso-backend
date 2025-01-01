package com.vlosco.backend.dto;

import java.time.LocalDateTime;

import com.vlosco.backend.model.User;

public class MessageResponseDTO {
    private Long messageId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime readAt;
    private User sender;
    private User receiver; 
    private Long conversationId;

    public MessageResponseDTO(Long messageId, String content, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime readAt, User sender, User receiver, Long conversationId) {
        this.messageId = messageId;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.readAt = readAt;
        this.sender = sender;
        this.receiver = receiver;
        this.conversationId = conversationId;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }
}
