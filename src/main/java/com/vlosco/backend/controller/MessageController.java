package com.vlosco.backend.controller;

import com.vlosco.backend.dto.MessageCreationDTO;
import com.vlosco.backend.dto.MessageResponseDTO;
import com.vlosco.backend.dto.MessageUpdateDTO;
import com.vlosco.backend.dto.ResponseDTO;
import com.vlosco.backend.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Contrôleur REST gérant toutes les opérations liées aux messages.
 * Fournit des endpoints pour la gestion des messages dans les conversations.
 */
@Tag(name = "Messages", description = "Endpoints pour la gestion des messages")
@RestController
@RequestMapping("/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @Operation(summary = "Récupérer tous les messages", description = "Retourne la liste de tous les messages pour un utilisateur spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des messages récupérée avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping
    public ResponseEntity<ResponseDTO<List<MessageResponseDTO>>> getAllMessages(
            @Parameter(description = "ID de l'utilisateur", required = true) @RequestParam Long userId) {
        return messageService.getAllMessages(userId);
    }

    @Operation(summary = "Récupérer un message par ID", description = "Retourne les détails d'un message spécifique basé sur son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message trouvé et retourné avec succès"),
            @ApiResponse(responseCode = "404", description = "Message non trouvé"),
            @ApiResponse(responseCode = "403", description = "Conversation inactive pour l'utilisateur"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<MessageResponseDTO>> getMessageById(
            @Parameter(description = "ID du message à récupérer", required = true) @PathVariable Long id,
            @Parameter(description = "ID de l'utilisateur qui initie la rêquete", required = true) @RequestParam Long userId) {
        return messageService.getMessageById(id, userId);
    }

    @Operation(summary = "Créer un nouveau message", description = "Crée un nouveau message dans une conversation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Message créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données de création de message invalides"),
            @ApiResponse(responseCode = "404", description = "Conversation ou expéditeur non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @PostMapping
    public ResponseEntity<ResponseDTO<MessageResponseDTO>> createMessage(
            @Parameter(description = "Données de création du message", required = true) @RequestBody MessageCreationDTO messageCreationDTO,
            @Parameter(description = "ID de l'expéditeur", required = true) @RequestParam Long senderId) {
        return messageService.createMessage(messageCreationDTO, senderId);
    }

    @Operation(summary = "Mettre à jour un message", description = "Met à jour le contenu d'un message existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Message non trouvé"),
            @ApiResponse(responseCode = "403", description = "Non autorisé à modifier ce message"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<MessageResponseDTO>> updateMessage(
            @Parameter(description = "ID du message à mettre à jour", required = true) @PathVariable Long id,
            @RequestBody MessageUpdateDTO messageUpdateDTO,
            @Parameter(description = "ID de l'utilisateur effectuant la mise à jour", required = true) @RequestParam Long userId) {
        return messageService.updateMessage(messageUpdateDTO, id, userId);
    }

    @Operation(summary = "Supprimer un message", description = "Supprime un message existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Message supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Message non trouvé"),
            @ApiResponse(responseCode = "403", description = "Non autorisé à supprimer ce message"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<Void>> deleteMessage(
            @Parameter(description = "ID du message à supprimer", required = true) @PathVariable Long id,
            @Parameter(description = "ID de l'utilisateur effectuant la suppression", required = true) @RequestParam Long userId) {
        return messageService.deleteMessage(id, userId);
    }

    @Operation(summary = "Marquer un message comme lu", description = "Marque un message spécifique comme lu pour un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message marqué comme lu avec succès"),
            @ApiResponse(responseCode = "404", description = "Message non trouvé"),
            @ApiResponse(responseCode = "403", description = "Non autorisé à marquer ce message comme lu"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @PostMapping("/{id}/read")
    public ResponseEntity<ResponseDTO<MessageResponseDTO>> markMessageAsRead(
            @Parameter(description = "ID du message à marquer comme lu", required = true) @PathVariable Long id,
            @Parameter(description = "ID de l'utilisateur", required = true) @RequestParam Long userId) {
        return messageService.markMessageAsRead(id, userId);
    }

    @Operation(summary = "Marquer tous les messages d'une conversation comme lus", 
              description = "Marque tous les messages non lus d'une conversation comme lus pour un utilisateur spécifique")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Messages marqués comme lus avec succès"),
        @ApiResponse(responseCode = "404", description = "Conversation non trouvée"),
        @ApiResponse(responseCode = "403", description = "Non autorisé"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @PostMapping("/conversation/{conversationId}/read-all")
    public ResponseEntity<ResponseDTO<List<MessageResponseDTO>>> markAllMessagesAsRead(
        @Parameter(description = "ID de la conversation", required = true) 
        @PathVariable Long conversationId,
        @Parameter(description = "ID de l'utilisateur", required = true) 
        @RequestParam Long userId) {
        return messageService.markAllMessagesAsRead(conversationId, userId);
    }
}