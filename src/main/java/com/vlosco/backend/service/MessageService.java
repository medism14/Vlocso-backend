package com.vlosco.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vlosco.backend.dto.ConversationResponseDTO;
import com.vlosco.backend.dto.MessageCreationDTO;
import com.vlosco.backend.dto.MessageResponseDTO;
import com.vlosco.backend.dto.MessageUpdateDTO;
import com.vlosco.backend.dto.ResponseDTO;
import com.vlosco.backend.model.Conversation;
import com.vlosco.backend.model.Message;
import com.vlosco.backend.model.User;
import com.vlosco.backend.repository.ConversationRepository;
import com.vlosco.backend.repository.MessageRepository;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final ConversationService conversationService;
    private final UserService userService;
    private final ConversationRepository conversationRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository, ConversationService conversationService,
            UserService userService, ConversationRepository conversationRepository) {
        this.messageRepository = messageRepository;
        this.conversationService = conversationService;
        this.userService = userService;
        this.conversationRepository = conversationRepository;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<ResponseDTO<List<MessageResponseDTO>>> getAllMessages(Long userId) {
        ResponseDTO<List<MessageResponseDTO>> response = new ResponseDTO<>();
        try {
            ResponseEntity<ResponseDTO<User>> userResponse = userService.getUserById(userId);
            if (userResponse.getStatusCode() != HttpStatus.OK || userResponse.getBody() == null) {
                return new ResponseEntity<>(new ResponseDTO<List<MessageResponseDTO>>("Utilisateur non trouvé"),
                        HttpStatus.NOT_FOUND);
            }
            User user;
            ResponseDTO<User> userResponseDTO = userResponse.getBody();

            if (userResponseDTO == null || userResponseDTO.getData() == null) {
                response.setMessage("L'utilisateur n'a pas été trouvé");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            } else {
                user = userResponseDTO.getData();
            }

            if (user == null) {
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            List<Message> messages = messageRepository.findAllMessagesForUser(user.getUserId());

            // Conversion des messages en MessageResponseDTO
            List<MessageResponseDTO> messageResponseDTOs = messages.stream()
                    .map(message -> new MessageResponseDTO(
                            message.getMessageId(),
                            message.getContent(),
                            message.getCreatedAt(),
                            message.getUpdatedAt(),
                            message.getReadTime(),
                            message.getSender(),
                            message.getReceiver(),
                            message.getConversation().getConversationId()))
                    .toList();

            response.setMessage("Messages récupérés avec succès");
            response.setData(messageResponseDTOs);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setMessage("Erreur lors de la récupération des messages");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<ResponseDTO<MessageResponseDTO>> getMessageById(Long messageId, Long userId) {
        ResponseDTO<MessageResponseDTO> response = new ResponseDTO<>();
        try {
            ResponseEntity<ResponseDTO<User>> userResponse = userService.getUserById(userId);
            if (userResponse.getStatusCode() != HttpStatus.OK || userResponse.getBody() == null) {
                return new ResponseEntity<>(new ResponseDTO<MessageResponseDTO>("Utilisateur non trouvé"),
                        HttpStatus.NOT_FOUND);
            }
            ResponseDTO<User> userResponseDTO = userResponse.getBody();
            User user;

            if (userResponseDTO == null || userResponseDTO.getData() == null) {
                response.setMessage("L'utilisateur n'a pas été trouvé");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            } else {
                user = userResponseDTO.getData();
            }
            Optional<Message> messageOpt = messageRepository.findById(messageId);
            if (messageOpt.isPresent()) {
                Message message = messageOpt.get();
                if (isConversationActiveForUser(message.getConversation(), user)) {
                    MessageResponseDTO messageResponseDTO = new MessageResponseDTO(
                            message.getMessageId(),
                            message.getContent(),
                            message.getCreatedAt(),
                            message.getUpdatedAt(),
                            message.getReadTime(),
                            message.getSender(),
                            message.getReceiver(),
                            message.getConversation().getConversationId());
                    response.setData(messageResponseDTO);
                    response.setMessage("Message trouvé");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    response.setMessage("Conversation inactive pour l'utilisateur");
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }
            }
            response.setMessage("Message non trouvé");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.setMessage("Erreur lors de la récupération du message");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<ResponseDTO<MessageResponseDTO>> createMessage(MessageCreationDTO messageCreationDTO,
            Long senderId) {
        ResponseDTO<MessageResponseDTO> response = new ResponseDTO<>();
        try {
            Long conversationId = messageCreationDTO.getConversationId();
            String content = messageCreationDTO.getContent();

            if (!isConversationActiveForUserWithId(conversationId, senderId)) {
                response.setMessage("Conversation inactive ou inéxistante pour l'utilisateur");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            ResponseEntity<ResponseDTO<ConversationResponseDTO>> conversationResponse = conversationService
                    .getConversationById(conversationId, senderId);

            if (conversationResponse.getStatusCode() != HttpStatus.OK || conversationResponse.getBody() == null) {
                response.setMessage("Conversation non trouvée");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            ResponseDTO<ConversationResponseDTO> conversationResponseDTO = conversationResponse.getBody();
            Conversation conversation;

            if (conversationResponseDTO == null || conversationResponseDTO.getData() == null) {
                response.setMessage("Conversation non trouvée ou invalide");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            } else {
                Optional<Conversation> optConversation = conversationRepository
                        .findById(conversationResponseDTO.getData().getConversationId());
                if (optConversation.isPresent()) {
                    conversation = optConversation.get();
                } else {
                    response.setMessage("Conversation non trouvée ou invalide");
                    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
                }
            }

            User sender;
            ResponseDTO<User> senderResponseDTO = userService.getUserById(senderId).getBody();

            if (senderResponseDTO == null || senderResponseDTO.getData() == null) {
                response.setMessage("Expéditeur non trouvé");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            } else {
                sender = senderResponseDTO.getData();
            }

            if (!isConversationActiveForUser(conversation, sender)) {
                response.setMessage("Conversation inactive pour l'utilisateur");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            Message message = new Message();
            message.setContent(content);
            message.setSender(sender);
            message.setReceiver(conversation.getBuyer().equals(sender) ? conversation.getAnnonce().getVendor()
                    : conversation.getBuyer());
            message.setConversation(conversation);

            Message savedMessage = messageRepository.save(message);
            MessageResponseDTO messageResponseDTO = new MessageResponseDTO(
                    savedMessage.getMessageId(),
                    savedMessage.getContent(),
                    savedMessage.getCreatedAt(),
                    savedMessage.getUpdatedAt(),
                    savedMessage.getReadTime(),
                    savedMessage.getSender(),
                    savedMessage.getReceiver(),
                    savedMessage.getConversation().getConversationId());
            response = new ResponseDTO<>(messageResponseDTO, "Message créé avec succès");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.setMessage("Erreur lors de la création du message");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<ResponseDTO<MessageResponseDTO>> updateMessage(MessageUpdateDTO messageUpdateDTO,
            Long messageId, Long userId) {
        ResponseDTO<MessageResponseDTO> response = new ResponseDTO<>();
        try {
            Optional<Message> messageOpt = messageRepository.findById(messageId);
            if (messageOpt.isPresent()) {
                Message message = messageOpt.get();

                User user;
                ResponseDTO<User> userResponseDTO = userService.getUserById(userId).getBody();

                if (userResponseDTO == null || userResponseDTO.getData() == null) {
                    response.setMessage("L'utilisateur n'a pas été trouvé");
                    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
                } else {
                    user = userResponseDTO.getData();
                }

                if (!isConversationActiveForUser(message.getConversation(), user)) {
                    response.setMessage("Conversation inactive pour l'utilisateur");
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }
                if (!message.getSender().equals(user)) {
                    response.setMessage("Non autorisé à modifier ce message");
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }
                message.setContent(messageUpdateDTO.getNewContent());
                Message updatedMessage = messageRepository.save(message);
                MessageResponseDTO messageResponseDTO = new MessageResponseDTO(
                        updatedMessage.getMessageId(),
                        updatedMessage.getContent(),
                        updatedMessage.getCreatedAt(),
                        updatedMessage.getUpdatedAt(),
                        updatedMessage.getReadTime(),
                        updatedMessage.getSender(),
                        updatedMessage.getReceiver(),
                        updatedMessage.getConversation().getConversationId());
                response = new ResponseDTO<>(messageResponseDTO, "Message mis à jour avec succès");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            response.setMessage("Message non trouvé");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.setMessage("Erreur lors de la mise à jour du message");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<ResponseDTO<Void>> deleteMessage(Long messageId, Long userId) {
        ResponseDTO<Void> response = new ResponseDTO<>();
        try {
            // Vérifier si le message existe
            Optional<Message> messageOpt = messageRepository.findById(messageId);
            if (!messageOpt.isPresent()) {
                response.setMessage("Message non trouvé");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            Message message = messageOpt.get();

            // Vérifier si l'utilisateur existe
            ResponseEntity<ResponseDTO<User>> userResponse = userService.getUserById(userId);
            User user;
            ResponseDTO<User> userResponseDTO = userResponse.getBody();

            if (userResponseDTO == null || userResponseDTO.getData() == null) {
                response.setMessage("L'utilisateur n'existe pas");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            } else {
                user = userResponseDTO.getData();
            }

            if (userResponse.getStatusCode() != HttpStatus.OK || userResponse.getBody() == null) {
                response.setMessage("L'utilisateur n'a pas été trouvé");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // Vérifier si la conversation est active pour l'utilisateur
            if (!isConversationActiveForUser(message.getConversation(), user)) {
                response.setMessage("Conversation inactive pour l'utilisateur");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            // Vérifier si l'utilisateur est l'expéditeur du message
            if (!message.getSender().equals(user)) {
                response.setMessage("Non autorisé à supprimer ce message");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            // Supprimer le message
            messageRepository.delete(message);
            response.setMessage("Message supprimé avec succès");
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);

        } catch (Exception e) {
            response.setMessage("Erreur lors de la suppression du message: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<ResponseDTO<MessageResponseDTO>> markMessageAsRead(Long messageId, Long userId) {
        ResponseDTO<MessageResponseDTO> response = new ResponseDTO<>();
        try {
            Optional<Message> messageOpt = messageRepository.findById(messageId);
            if (messageOpt.isPresent()) {
                Message message = messageOpt.get();
                User user;
                ResponseDTO<User> userResponseDTO = userService.getUserById(userId).getBody();

                if (userResponseDTO == null || userResponseDTO.getData() == null) {
                    response.setMessage("L'utilisateur n'a pas été trouvé");
                    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
                } else {
                    user = userResponseDTO.getData();
                }

                if (!isConversationActiveForUser(message.getConversation(), user)) {
                    response.setMessage("Conversation inactive pour l'utilisateur");
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }

                if (!message.getReceiver().equals(user)) {
                    response.setMessage("Non autorisé à marquer ce message comme lu");
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }

                message.setReadTime(LocalDateTime.now());
                Message updatedMessage = messageRepository.save(message);
                MessageResponseDTO messageResponseDTO = new MessageResponseDTO(
                        updatedMessage.getMessageId(),
                        updatedMessage.getContent(),
                        updatedMessage.getCreatedAt(),
                        updatedMessage.getUpdatedAt(),
                        updatedMessage.getReadTime(),
                        updatedMessage.getSender(),
                        updatedMessage.getReceiver(),
                        updatedMessage.getConversation().getConversationId());
                response = new ResponseDTO<>(messageResponseDTO, "Message marqué comme lu avec succès");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            response.setMessage("Message non trouvé");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.setMessage("Erreur lors du marquage du message comme lu");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    private boolean isConversationActiveForUser(Conversation conversation, User user) {
        if (conversation.getBuyer().equals(user)) {
            return conversation.isActiveForBuyer();
        } else if (conversation.getAnnonce().getVendor().equals(user)) {
            return conversation.isActiveForVendor();
        }
        return false;
    }

    @Transactional(readOnly = true)
    private boolean isConversationActiveForUserWithId(Long conversationId, Long userId) {
        Optional<Conversation> conversationOptional = conversationRepository.findById(conversationId);
        ResponseDTO<User> userResponseDTO = userService.getUserById(userId).getBody();
        User user;

        if (userResponseDTO == null || userResponseDTO.getData() == null) {
            return false;
        } else {
            user = userResponseDTO.getData();
        }

        if (conversationOptional.isPresent()) {
            Conversation conversation = conversationOptional.get();

            if (conversation.getBuyer().equals(user)) {
                return conversation.isActiveForBuyer();
            } else if (conversation.getAnnonce().getVendor().equals(user)) {
                return conversation.isActiveForVendor();
            }
        }
        return false;
    }
}