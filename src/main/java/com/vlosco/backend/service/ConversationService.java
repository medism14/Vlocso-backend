package com.vlosco.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.vlosco.backend.dto.AnnonceWithUserDTO;
import com.vlosco.backend.dto.ConversationCreationDTO;
import com.vlosco.backend.dto.ConversationResponseDTO;
import com.vlosco.backend.dto.ConversationUpdateDTO;
import com.vlosco.backend.dto.MessageResponseDTO;
import com.vlosco.backend.dto.ResponseDTO;
import com.vlosco.backend.model.Annonce;
import com.vlosco.backend.model.Conversation;
import com.vlosco.backend.model.Message;
import com.vlosco.backend.model.User;
import com.vlosco.backend.repository.ConversationRepository;
import com.vlosco.backend.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConversationService {
    private final ConversationRepository conversationRepository;
    private final AnnonceService annonceService;
    private final UserRepository userRepository;

    @Autowired
    public ConversationService(ConversationRepository conversationRepository, AnnonceService annonceService,
            UserRepository userRepository) {
        this.conversationRepository = conversationRepository;
        this.annonceService = annonceService;
        this.userRepository = userRepository;
    }

    private List<MessageResponseDTO> convertToMessageResponseDTOs(List<Message> messages) {
        return messages.stream()
            .map(message -> new MessageResponseDTO(
                message.getMessageId(),
                message.getContent(), 
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getReadTime(),
                message.getSender(),
                message.getReceiver(),
                message.getConversation().getConversationId()
            ))
            .collect(Collectors.toList());
    }

    public ResponseEntity<ResponseDTO<List<ConversationResponseDTO>>> getAllConversations() {
        ResponseDTO<List<ConversationResponseDTO>> response;
        try {
            List<Conversation> conversations = conversationRepository.findAll();
            List<ConversationResponseDTO> conversationResponseDTOs = conversations.stream().map(conversation -> {
                ConversationResponseDTO dto = new ConversationResponseDTO();
                dto.setConversationId(conversation.getConversationId());
                dto.setAnnonceWithUserDto(annonceService.convertToAnnonceWithUserDTO(conversation.getAnnonce()));
                dto.setBuyer(conversation.getBuyer());
                dto.setVendor(conversation.getAnnonce().getVendor());
                dto.setActiveForBuyer(conversation.isActiveForBuyer());
                dto.setActiveForVendor(conversation.isActiveForVendor());
                dto.setMessages(convertToMessageResponseDTOs(conversation.getMessages()));
                dto.setCreatedAt(conversation.getCreatedAt());
                return dto;
            }).toList();
            response = new ResponseDTO<>(conversationResponseDTOs, "Conversations récupérées avec succès");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response = new ResponseDTO<>("Erreur lors de la récupération des conversations");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResponseDTO<ConversationResponseDTO>> createConversation(
            ConversationCreationDTO conversationCreationDTO) {
        ResponseDTO<ConversationResponseDTO> response;
        try {
            Long annonceId = conversationCreationDTO.getAnnonceId();
            Long buyerId = conversationCreationDTO.getBuyerId();

            // Vérifier si une conversation existe déjà avant de récupérer l'annonce et l'acheteur
            Optional<Conversation> existingConversation = conversationRepository.findByAnnonceIdAndBuyerId(annonceId, buyerId);
            if (existingConversation.isPresent()) {
                // Convertir la conversation existante en DTO et la retourner
                Conversation conversation = existingConversation.get();
                ConversationResponseDTO conversationResponseDTO = new ConversationResponseDTO();
                conversationResponseDTO.setConversationId(conversation.getConversationId());
                conversationResponseDTO.setAnnonceWithUserDto(annonceService.convertToAnnonceWithUserDTO(conversation.getAnnonce()));
                conversationResponseDTO.setBuyer(conversation.getBuyer());
                conversationResponseDTO.setVendor(conversation.getAnnonce().getVendor());
                conversationResponseDTO.setActiveForBuyer(conversation.isActiveForBuyer());
                conversationResponseDTO.setActiveForVendor(conversation.isActiveForVendor());
                conversationResponseDTO.setMessages(convertToMessageResponseDTOs(conversation.getMessages()));
                conversationResponseDTO.setCreatedAt(conversation.getCreatedAt());
                
                response = new ResponseDTO<>(conversationResponseDTO, "Conversation existante récupérée");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

            // Récupérer l'annonce et l'acheteur par leurs identifiants
            ResponseEntity<ResponseDTO<AnnonceWithUserDTO>> annonceResponse = annonceService.getAnnonceById(annonceId);
            Optional<User> buyerOptional = userRepository.findById(buyerId);

            if (annonceResponse.getStatusCode() != HttpStatus.OK || annonceResponse.getBody() == null
                    || buyerOptional.isEmpty()) {
                response = new ResponseDTO<>("Annonce ou acheteur non trouvé");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            ResponseDTO<AnnonceWithUserDTO> annonceResponseDTO = annonceResponse.getBody();
            Annonce annonce;

            if (annonceResponseDTO == null || annonceResponseDTO.getData() == null
                    || annonceResponseDTO.getData().getAnnonce() == null) {
                response = new ResponseDTO<>("Annonce non trouvée ou invalide");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            } else {
                annonce = annonceResponseDTO.getData().getAnnonce();
            }

            User buyer = buyerOptional.get();
            User vendor = annonce.getVendor();

            Conversation conversation = new Conversation();
            conversation.setAnnonce(annonce);
            conversation.setBuyer(buyer);
            conversation.setActiveForBuyer(true);
            conversation.setActiveForVendor(true);

            Conversation savedConversation = conversationRepository.save(conversation);

            // Créer le DTO de réponse
            ConversationResponseDTO conversationResponseDTO = new ConversationResponseDTO();
            conversationResponseDTO.setConversationId(savedConversation.getConversationId());
            conversationResponseDTO.setAnnonceWithUserDto(annonceService.convertToAnnonceWithUserDTO(annonce));
            conversationResponseDTO.setBuyer(buyer);
            conversationResponseDTO.setVendor(vendor);
            conversationResponseDTO.setActiveForBuyer(true);
            conversationResponseDTO.setActiveForVendor(true);
            conversationResponseDTO.setMessages(convertToMessageResponseDTOs(savedConversation.getMessages()));
            conversationResponseDTO.setCreatedAt(savedConversation.getCreatedAt());

            response = new ResponseDTO<>(conversationResponseDTO, "Conversation créée avec succès");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace(); // Pour avoir plus de détails sur l'erreur
            response = new ResponseDTO<>("Erreur lors de la création de la conversation: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResponseDTO<List<ConversationResponseDTO>>> getConversationsByUser(Long userId) {
        ResponseDTO<List<ConversationResponseDTO>> response;
        try {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                Optional<List<Conversation>> userConversationsOptional = conversationRepository
                        .findConversationsByUser(user.getUserId());
                List<Conversation> userConversations = userConversationsOptional.orElseGet(Collections::emptyList);

                List<ConversationResponseDTO> conversationResponseDTOs = userConversations.stream()
                        .map(conversation -> {
                            ConversationResponseDTO dto = new ConversationResponseDTO();
                            dto.setConversationId(conversation.getConversationId());
                            dto.setAnnonceWithUserDto(annonceService.convertToAnnonceWithUserDTO(conversation.getAnnonce()));
                            dto.setBuyer(conversation.getBuyer());
                            dto.setVendor(conversation.getAnnonce().getVendor());
                            dto.setActiveForBuyer(conversation.isActiveForBuyer());
                            dto.setActiveForVendor(conversation.isActiveForVendor());
                            dto.setMessages(convertToMessageResponseDTOs(conversation.getMessages()));
                            dto.setCreatedAt(conversation.getCreatedAt());
                            return dto;
                        })
                        .collect(Collectors.toList());

                response = new ResponseDTO<>(conversationResponseDTOs, "Conversations récupérées avec succès");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response = new ResponseDTO<>("Utilisateur non trouvé");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            response = new ResponseDTO<>("Erreur lors de la récupération des conversations");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResponseDTO<ConversationResponseDTO>> getConversationById(Long conversationId, Long userId) {
        ResponseDTO<ConversationResponseDTO> response;
        try {
            Optional<Conversation> conversationOptional = conversationRepository.findById(conversationId);
            if (!conversationOptional.isPresent()) {
                response = new ResponseDTO<>("Conversation non trouvée");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            if (!isUserInConversation(conversationId, userId)) {
                response = new ResponseDTO<>("Utilisateur non autorisé");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            Conversation conversation = conversationOptional.get();
            ConversationResponseDTO conversationResponseDTO = new ConversationResponseDTO();
            conversationResponseDTO.setConversationId(conversation.getConversationId());
            conversationResponseDTO.setAnnonceWithUserDto(annonceService.convertToAnnonceWithUserDTO(conversation.getAnnonce()));
            conversationResponseDTO.setBuyer(conversation.getBuyer());
            conversationResponseDTO.setVendor(conversation.getAnnonce().getVendor());
            conversationResponseDTO.setActiveForBuyer(conversation.isActiveForBuyer());
            conversationResponseDTO.setActiveForVendor(conversation.isActiveForVendor());
            conversationResponseDTO.setMessages(convertToMessageResponseDTOs(conversation.getMessages()));
            conversationResponseDTO.setCreatedAt(conversation.getCreatedAt());
            response = new ResponseDTO<>(conversationResponseDTO, "Conversation trouvée");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response = new ResponseDTO<>("Erreur lors de la récupération de la conversation");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResponseDTO<ConversationResponseDTO>> updateConversation(Long conversationId,
            ConversationUpdateDTO updatedConversation, Long userId) {
        ResponseDTO<ConversationResponseDTO> response;
        try {
            Optional<Conversation> conversationOptional = conversationRepository.findById(conversationId);
            if (!conversationOptional.isPresent()) {
                response = new ResponseDTO<>("Conversation non trouvée");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            if (!isUserInConversation(conversationId, userId)) {
                response = new ResponseDTO<>("Utilisateur non autorisé");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            Conversation conversation = conversationOptional.get();
            conversation.setActiveForBuyer(updatedConversation.isActiveForBuyer());
            conversation.setActiveForVendor(updatedConversation.isActiveForVendor());

            Conversation savedConversation = conversationRepository.save(conversation);
            ConversationResponseDTO conversationResponseDTO = new ConversationResponseDTO();
            conversationResponseDTO.setConversationId(savedConversation.getConversationId());
            conversationResponseDTO.setAnnonceWithUserDto(annonceService.convertToAnnonceWithUserDTO(savedConversation.getAnnonce()));
            conversationResponseDTO.setBuyer(savedConversation.getBuyer());
            conversationResponseDTO.setVendor(savedConversation.getAnnonce().getVendor());
            conversationResponseDTO.setActiveForBuyer(savedConversation.isActiveForBuyer());
            conversationResponseDTO.setActiveForVendor(savedConversation.isActiveForVendor());
            conversationResponseDTO.setMessages(convertToMessageResponseDTOs(savedConversation.getMessages()));
            conversationResponseDTO.setCreatedAt(savedConversation.getCreatedAt());

            response = new ResponseDTO<>(conversationResponseDTO, "Conversation mise à jour avec succès");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response = new ResponseDTO<>("Erreur lors de la mise à jour de la conversation");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResponseDTO<Void>> deleteConversation(Long conversationId, Long userId) {
        ResponseDTO<Void> response;
        try {
            Optional<Conversation> conversationOptional = conversationRepository.findById(conversationId);
            if (!conversationOptional.isPresent()) {
                response = new ResponseDTO<>("Conversation non trouvée");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            if (!isUserInConversation(conversationId, userId)) {
                response = new ResponseDTO<>("Utilisateur non autorisé");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            Optional<User> userOptional = userRepository.findById(userId);
            if (!userOptional.isPresent()) {
                response = new ResponseDTO<>("Utilisateur non trouvé");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            Conversation conversation = conversationOptional.get();
            User user = userOptional.get();

            if (conversation.getBuyer().equals(user)) {
                conversation.setActiveForBuyer(false);
            } else if (conversation.getAnnonce().getVendor().equals(user)) {
                conversation.setActiveForVendor(false);
            }

            // Enregistrer la conversation avant de la supprimer
            conversationRepository.save(conversation);

            if (!conversation.isActiveForBuyer() && !conversation.isActiveForVendor()) {
                conversationRepository.delete(conversation);
            }

            response = new ResponseDTO<>("Conversation supprimée avec succès");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response = new ResponseDTO<>("Erreur lors de la suppression de la conversation");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public boolean isUserInConversation(Long conversationId, Long userId) {
        Optional<Conversation> conversationOptional = conversationRepository.findById(conversationId);
        Optional<User> userOptional = userRepository.findById(userId);

        if (conversationOptional.isPresent() && userOptional.isPresent()) {
            Conversation conversation = conversationOptional.get();
            User user = userOptional.get();

            boolean isUserBuyer = conversation.getBuyer().equals(user);
            boolean isUserVendor = conversation.getAnnonce().getVendor().equals(user);
            boolean isActiveForBuyer = isUserBuyer && conversation.isActiveForBuyer();
            boolean isActiveForVendor = isUserVendor && conversation.isActiveForVendor();

            return isActiveForBuyer || isActiveForVendor;
        }
        return false;
    }
}
