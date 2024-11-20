package com.vlosco.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vlosco.backend.model.Conversation;

public interface ConversationRepository extends JpaRepository<Conversation, Long>{

}
