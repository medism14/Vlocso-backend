package com.vlosco.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vlosco.backend.model.Message;

public interface MessageRepository extends JpaRepository<Message, Long>{

}
