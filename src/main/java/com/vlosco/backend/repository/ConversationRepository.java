package com.vlosco.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import com.vlosco.backend.model.Conversation;
import com.vlosco.backend.model.User;
import com.vlosco.backend.model.Annonce;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    @Query("SELECT c FROM Conversation c WHERE (c.buyer.id = :userId AND c.isActiveForBuyer = true) OR (c.annonce.vendor.id = :userId AND c.isActiveForVendor = true)")
    Optional<List<Conversation>> findConversationsByUser(@Param("userId") Long userId);

    Optional<Conversation> findByAnnonceAndBuyer(Annonce annonce, User buyer);
}
