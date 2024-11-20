package com.vlosco.backend.service;

import org.springframework.stereotype.Service;

import com.vlosco.backend.repository.MessageRepository;

@Service
public class MessageService {
    private MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }
}
