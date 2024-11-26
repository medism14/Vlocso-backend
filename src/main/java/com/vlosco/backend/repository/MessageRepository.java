package com.vlosco.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import com.vlosco.backend.model.Message;
import com.vlosco.backend.model.Conversation;
import com.vlosco.backend.model.User;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT m FROM Message m WHERE m.conversation = :conversation ORDER BY m.createdAt DESC")
    List<Message> findByConversationOrderByCreatedAtDesc(@Param("conversation") Conversation conversation);

    @Query("SELECT m FROM Message m WHERE m.receiver = :receiver AND m.readTime IS NULL")
    List<Message> findByReceiverAndReadTimeIsNull(@Param("receiver") User receiver);

    @Query("SELECT m FROM Message m WHERE (m.sender = :user1 AND m.receiver = :user2) OR (m.sender = :user2 AND m.receiver = :user1) ORDER BY m.createdAt DESC")
    List<Message> findBySenderAndReceiverOrReceiverAndSenderOrderByCreatedAtDesc(@Param("user1") User user1,
            @Param("user2") User user2);

    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId ORDER BY m.createdAt DESC")
    List<Message> findByConversationIdOrderByCreatedAtDesc(@Param("conversationId") Long conversationId);

    // Nouvelle méthode pour récupérer tous les messages d'un utilisateur
    @Query("SELECT m FROM Message m WHERE m.sender.id = :userId OR m.receiver.id = :userId ORDER BY m.createdAt DESC")
    List<Message> findAllMessagesForUser(@Param("userId") Long userId);
}
