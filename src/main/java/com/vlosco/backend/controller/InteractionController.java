package com.vlosco.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.vlosco.backend.dto.ResponseDTO;
import com.vlosco.backend.model.Interaction;
import com.vlosco.backend.service.InteractionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/interactions")
@CrossOrigin(origins = "*")
@Tag(name = "Interactions", description = "API de gestion des interactions utilisateurs")
public class InteractionController {

    private final InteractionService interactionService;

    @Autowired
    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    @Operation(
        summary = "Créer une nouvelle interaction",
        description = "Crée une nouvelle interaction utilisateur avec une annonce"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Interaction créée avec succès",
            content = @Content(schema = @Schema(implementation = Interaction.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Erreur lors de la création de l'interaction"
        )
    })
    @PostMapping("/create")
    public ResponseEntity<ResponseDTO<Interaction>> createInteraction(
            @RequestBody Interaction interaction) {
        return interactionService.createInteraction(interaction);
    }

    @Operation(
        summary = "Récupérer toutes les interactions",
        description = "Retourne la liste de toutes les interactions"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Liste des interactions récupérée avec succès"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Erreur lors de la récupération des interactions"
        )
    })
    @GetMapping("/all")
    public ResponseEntity<ResponseDTO<List<Interaction>>> getAllInteractions() {
        return interactionService.getAllInteractions();
    }

    @Operation(
        summary = "Récupérer une interaction par ID",
        description = "Retourne une interaction spécifique basée sur son ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Interaction trouvée"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Interaction non trouvée"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Erreur lors de la récupération de l'interaction"
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<Interaction>> getInteractionById(
            @Parameter(description = "ID de l'interaction") @PathVariable Long id) {
        return interactionService.getInteractionById(id);
    }

    @Operation(
        summary = "Récupérer les interactions d'un utilisateur",
        description = "Retourne toutes les interactions d'un utilisateur spécifique"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Interactions de l'utilisateur récupérées avec succès"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Utilisateur non trouvé"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Erreur lors de la récupération des interactions"
        )
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseDTO<List<Interaction>>> getInteractionsByUserId(
            @Parameter(description = "ID de l'utilisateur") @PathVariable Long userId) {
        return interactionService.getInteractionsByUserId(userId);
    }

    @Operation(
        summary = "Récupérer les recherches d'un utilisateur",
        description = "Retourne l'historique des recherches d'un utilisateur"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Historique des recherches récupéré avec succès"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Utilisateur non trouvé"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Erreur lors de la récupération de l'historique"
        )
    })
    @GetMapping("/user/{userId}/searches")
    public ResponseEntity<ResponseDTO<List<Interaction>>> getUserSearchHistory(
            @Parameter(description = "ID de l'utilisateur") @PathVariable Long userId) {
        return interactionService.getUserSearchHistory(userId);
    }

    @Operation(
        summary = "Mettre à jour une interaction",
        description = "Met à jour une interaction existante"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Interaction mise à jour avec succès",
            content = @Content(schema = @Schema(implementation = Interaction.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Interaction non trouvée"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Erreur lors de la mise à jour de l'interaction"
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<Interaction>> updateInteraction(
            @Parameter(description = "ID de l'interaction") @PathVariable Long id,
            @RequestBody Interaction interaction) {
        return interactionService.updateInteraction(id, interaction);
    }

    @Operation(
        summary = "Supprimer une interaction",
        description = "Supprime une interaction spécifique"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Interaction supprimée avec succès"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Interaction non trouvée"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Erreur lors de la suppression de l'interaction"
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<Void>> deleteInteraction(
            @Parameter(description = "ID de l'interaction") @PathVariable Long id) {
        return interactionService.deleteInteraction(id);
    }
}
