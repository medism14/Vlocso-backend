package com.vlosco.backend.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import com.vlosco.backend.dto.AnnonceFilterDTO;
import com.vlosco.backend.dto.ResponseDTO;
import com.vlosco.backend.model.Annonce;
import com.vlosco.backend.service.AnnonceFilterService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Filtre Annonces", description = "API pour le filtrage des annonces")
@RestController
@RequestMapping("/api/annonces/filter")
@CrossOrigin(origins = "*")
public class AnnonceFilterController {

    private final AnnonceFilterService filterService;

    public AnnonceFilterController(AnnonceFilterService filterService) {
        this.filterService = filterService;
    }

    @Operation(summary = "Filtrer une liste d'annonces", 
              description = "Filtre et trie une liste d'annonces selon les critères spécifiés")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Filtrage effectué avec succès"),
        @ApiResponse(responseCode = "400", description = "Paramètres de filtrage invalides"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @PostMapping
    public ResponseEntity<ResponseDTO<List<Annonce>>> filterAnnonces(
        @Parameter(description = "Critères de filtrage") 
        @RequestBody AnnonceFilterDTO filterDTO) {
        return filterService.filterAnnonces(filterDTO);
    }
} 