package com.vlosco.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import com.vlosco.backend.repository.InteractionRepository;
import com.vlosco.backend.model.Interaction;

@Service
public class InteractionService {
    private final InteractionRepository interactionRepository;

    @Autowired
    public InteractionService(InteractionRepository interactionRepository) {
        this.interactionRepository = interactionRepository;
    }

    public Interaction createInteraction(Interaction interaction) {
        return interactionRepository.save(interaction);
    }

    public List<Interaction> getAllInteractions() {
        return interactionRepository.findAll();
    }

    public Optional<Interaction> getInteractionById(Long id) {
        return interactionRepository.findById(id);
    }

    public List<Interaction> getInteractionsByUserId(Long userId) {
        return interactionRepository.findByUserId(userId);
    }

    public Interaction updateInteraction(Long id, Interaction interactionDetails) {
        Optional<Interaction> interaction = interactionRepository.findById(id);
        if (interaction.isPresent()) {
            Interaction existingInteraction = interaction.get();
            existingInteraction.setInteractionType(interactionDetails.getInteractionType());
            existingInteraction.setContent(interactionDetails.getContent());
            existingInteraction.setAnnonce(interactionDetails.getAnnonce());
            existingInteraction.setUser(interactionDetails.getUser());
            return interactionRepository.save(existingInteraction);
        }
        return null;
    }

    public void deleteInteraction(Long id) {
        interactionRepository.deleteById(id);
    }
}
