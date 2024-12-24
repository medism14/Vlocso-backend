package com.vlosco.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.vlosco.backend.model.Interaction;
import com.vlosco.backend.service.InteractionService;

@RestController
@RequestMapping("/api/interactions")
@CrossOrigin(origins = "*")
public class InteractionController {
    
    private final InteractionService interactionService;

    @Autowired
    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    @PostMapping
    public ResponseEntity<Interaction> createInteraction(@RequestBody Interaction interaction) {
        Interaction newInteraction = interactionService.createInteraction(interaction);
        return ResponseEntity.ok(newInteraction);
    }

    @GetMapping
    public ResponseEntity<List<Interaction>> getAllInteractions() {
        List<Interaction> interactions = interactionService.getAllInteractions();
        return ResponseEntity.ok(interactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Interaction> getInteractionById(@PathVariable Long id) {
        return interactionService.getInteractionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Interaction>> getInteractionsByUserId(@PathVariable Long userId) {
        List<Interaction> interactions = interactionService.getInteractionsByUserId(userId);
        return ResponseEntity.ok(interactions);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Interaction> updateInteraction(@PathVariable Long id, @RequestBody Interaction interaction) {
        Interaction updatedInteraction = interactionService.updateInteraction(id, interaction);
        if (updatedInteraction != null) {
            return ResponseEntity.ok(updatedInteraction);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInteraction(@PathVariable Long id) {
        interactionService.deleteInteraction(id);
        return ResponseEntity.ok().build();
    }
}
