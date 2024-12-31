package com.vlosco.backend.controller;

import com.vlosco.backend.dto.ConversationCreationDTO;
import com.vlosco.backend.dto.ConversationResponseDTO;
import com.vlosco.backend.dto.ConversationUpdateDTO;
import com.vlosco.backend.dto.ResponseDTO;
import com.vlosco.backend.service.ConversationService;
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
 * Contrôleur REST gérant toutes les opérations liées aux conversations.
 * Fournit des endpoints pour la gestion des conversations entre utilisateurs.
 */
@Tag(name = "Conversations", description = "Endpoints pour la gestion des conversations")
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    @Autowired
    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @Operation(summary = "Récupérer toutes les conversations", description = "Retourne la liste de toutes les conversations disponibles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des conversations récupérée avec succès"),
            @ApiResponse(responseCode = "204", description = "Aucune conversation trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping
    public ResponseEntity<ResponseDTO<List<ConversationResponseDTO>>> getAllConversations() {
        return conversationService.getAllConversations();
    }

    @Operation(summary = "Créer une nouvelle conversation", description = "Crée une nouvelle conversation entre un acheteur et une annonce")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Conversation créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides fournies"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @PostMapping
    public ResponseEntity<ResponseDTO<ConversationResponseDTO>> createConversation(
            @Parameter(description = "ID de l'annonce et du buyer", required = true) @RequestBody ConversationCreationDTO conversationCreationDTO) {
        return conversationService.createConversation(conversationCreationDTO);
    }

    @Operation(summary = "Mettre à jour une conversation", description = "Met à jour les détails d'une conversation existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversation mise à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Conversation non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur lors de la mise à jour")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<ConversationResponseDTO>> updateConversation(
            @Parameter(description = "ID de la conversation à mettre à jour", required = true) @PathVariable Long id,
            @Parameter(description = "Nouveaux détails de la conversation", required = true) @RequestBody ConversationUpdateDTO updatedConversation,
            @Parameter(description = "ID de l'utilisateur effectuant la mise à jour", required = true) @RequestParam Long userId) {
        return conversationService.updateConversation(id, updatedConversation, userId);
    }

    @Operation(summary = "Supprimer une conversation", description = "Supprime une conversation existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversation supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Conversation non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur lors de la suppression")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<Void>> deleteConversation(
            @Parameter(description = "ID de la conversation à supprimer", required = true) @PathVariable Long id,
            @Parameter(description = "ID de l'utilisateur effectuant la suppression", required = true) @RequestParam Long userId) {
        return conversationService.deleteConversation(id, userId);
    }

    @Operation(summary = "Récupérer une conversation par ID", description = "Retourne les détails d'une conversation spécifique basée sur son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversation trouvée et retournée avec succès"),
            @ApiResponse(responseCode = "404", description = "Conversation non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<ConversationResponseDTO>> getConversationById(
            @Parameter(description = "ID de la conversation à récupérer", required = true) @PathVariable Long id,
            @Parameter(description = "ID de l'utilisateur effectuant la récupération", required = true) @RequestParam Long userId) {
        return conversationService.getConversationById(id, userId);
    }

    @Operation(summary = "Récupérer les conversations d'un utilisateur", description = "Retourne la liste des conversations associées à un utilisateur spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversations de l'utilisateur récupérées avec succès"),
            @ApiResponse(responseCode = "404", description = "Aucune conversation trouvée pour cet utilisateur"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseDTO<List<ConversationResponseDTO>>> getConversationsByUser(
            @Parameter(description = "ID de l'utilisateur dont on veut récupérer les conversations", required = true) @PathVariable Long userId) {
        return conversationService.getConversationsByUser(userId);
    }
}
