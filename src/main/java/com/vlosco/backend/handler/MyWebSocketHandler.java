package com.vlosco.backend.handler;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import java.util.logging.Logger;
import java.util.logging.Level;
import com.vlosco.backend.dto.MessageResponseDTO;

@Controller
public class MyWebSocketHandler {
    
    private static final Logger logger = Logger.getLogger(MyWebSocketHandler.class.getName());

    @MessageMapping("/chat/{conversationId}")
    @SendTo("/topic/conversation/{conversationId}")
    public MessageResponseDTO handleChatMessage(@Payload MessageResponseDTO message) {
        try {
            logger.info("Message reçu pour la conversation " + message.getConversationId());
            return message;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors du traitement du message", e);
            return null;
        }
    }
}
