package com.vlosco.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import com.vlosco.backend.repository.InteractionRepository;
import com.vlosco.backend.dto.ResponseDTO;
import com.vlosco.backend.model.Interaction;

@Service
public class InteractionService {
    private final InteractionRepository interactionRepository;

    @Autowired
    public InteractionService(InteractionRepository interactionRepository) {
        this.interactionRepository = interactionRepository;
    }

    public ResponseEntity<ResponseDTO<Interaction>> createInteraction(Interaction interaction) {
        ResponseDTO<Interaction> response = new ResponseDTO<>();
        try {
            Interaction savedInteraction = interactionRepository.save(interaction);
            response.setData(savedInteraction);
            response.setMessage("Interaction créée avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage("Erreur lors de la création de l'interaction: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<ResponseDTO<List<Interaction>>> getAllInteractions() {
        ResponseDTO<List<Interaction>> response = new ResponseDTO<>();
        try {
            List<Interaction> interactions = interactionRepository.findAll();
            response.setData(interactions);
            response.setMessage("Interactions récupérées avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage("Erreur lors de la récupération des interactions: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<ResponseDTO<Interaction>> getInteractionById(Long id) {
        ResponseDTO<Interaction> response = new ResponseDTO<>();
        try {
            Optional<Interaction> interaction = interactionRepository.findById(id);
            if (interaction.isPresent()) {
                response.setData(interaction.get());
                response.setMessage("Interaction trouvée");
                return ResponseEntity.ok(response);
            } else {
                response.setMessage("Interaction non trouvée");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.setMessage("Erreur lors de la récupération de l'interaction: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<ResponseDTO<List<Interaction>>> getInteractionsByUserId(Long userId) {
        ResponseDTO<List<Interaction>> response = new ResponseDTO<>();
        try {
            List<Interaction> interactions = interactionRepository.findByUserId(userId);
            response.setData(interactions);
            response.setMessage("Interactions de l'utilisateur récupérées avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage("Erreur lors de la récupération des interactions: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<ResponseDTO<List<Interaction>>> getUserSearchHistory(Long userId) {
        ResponseDTO<List<Interaction>> response = new ResponseDTO<>();
        try {
            Optional<List<Interaction>> searchHistory = interactionRepository.findByUserSearchs(userId);
            response.setData(searchHistory.orElse(List.of()));
            response.setMessage("Historique des recherches récupéré avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage("Erreur lors de la récupération de l'historique: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<ResponseDTO<Interaction>> updateInteraction(Long id, Interaction interactionDetails) {
        ResponseDTO<Interaction> response = new ResponseDTO<>();
        try {
            Optional<Interaction> interaction = interactionRepository.findById(id);
            if (interaction.isPresent()) {
                Interaction existingInteraction = interaction.get();
                existingInteraction.setInteractionType(interactionDetails.getInteractionType());
                existingInteraction.setContent(interactionDetails.getContent());
                existingInteraction.setAnnonce(interactionDetails.getAnnonce());
                existingInteraction.setUser(interactionDetails.getUser());
                
                Interaction updatedInteraction = interactionRepository.save(existingInteraction);
                response.setData(updatedInteraction);
                response.setMessage("Interaction mise à jour avec succès");
                return ResponseEntity.ok(response);
            } else {
                response.setMessage("Interaction non trouvée");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.setMessage("Erreur lors de la mise à jour de l'interaction: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<ResponseDTO<Void>> deleteInteraction(Long id) {
        ResponseDTO<Void> response = new ResponseDTO<>();
        try {
            if (interactionRepository.existsById(id)) {
                interactionRepository.deleteById(id);
                response.setMessage("Interaction supprimée avec succès");
                return ResponseEntity.ok(response);
            } else {
                response.setMessage("Interaction non trouvée");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.setMessage("Erreur lors de la suppression de l'interaction: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
