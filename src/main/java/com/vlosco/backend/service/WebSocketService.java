package com.vlosco.backend.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.vlosco.backend.dto.MessageResponseDTO;

@Service
public class WebSocketService {
    
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyNewMessage(Long conversationId, MessageResponseDTO message) {
        messagingTemplate.convertAndSend("/topic/conversation/" + conversationId, message);
    }
} 