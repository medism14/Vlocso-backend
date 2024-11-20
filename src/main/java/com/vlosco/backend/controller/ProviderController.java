/**
 * Contrôleur REST pour la gestion des fournisseurs (providers).
 * Fournit les endpoints pour effectuer les opérations CRUD sur les fournisseurs.
 */
package com.vlosco.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.vlosco.backend.dto.CreateProviderDTO;
import com.vlosco.backend.dto.ResponseDTO;
import com.vlosco.backend.dto.UpdateProviderDTO;
import com.vlosco.backend.model.Provider;
import com.vlosco.backend.service.ProviderService;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Providers", description = "API pour la gestion des fournisseurs")
@RestController
@RequestMapping("/providers")
@CrossOrigin(origins = "*")
public class ProviderController {

    private final ProviderService providerService;

    @Autowired
    public ProviderController(ProviderService providerService) {
        this.providerService = providerService;
    }

    @Operation(summary = "Récupérer tous les fournisseurs",
              description = "Retourne la liste complète des fournisseurs")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des fournisseurs récupérée avec succès"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping
    public ResponseEntity<ResponseDTO<List<Provider>>> getAllProviders() {
        return providerService.getAllProviders();
    }

    @Operation(summary = "Récupérer un fournisseur par ID",
              description = "Retourne un fournisseur spécifique basé sur son ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Fournisseur trouvé"),
        @ApiResponse(responseCode = "404", description = "Fournisseur non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<Provider>> getProviderById(
        @Parameter(description = "ID du fournisseur à récupérer") @PathVariable Long id) {
        return providerService.getProviderById(id);
    }

    @Operation(summary = "Créer un nouveau fournisseur",
              description = "Crée un nouveau fournisseur avec les informations fournies")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Fournisseur créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @PostMapping
    public ResponseEntity<ResponseDTO<Provider>> createProvider(
        @Parameter(description = "Données de création du fournisseur") @RequestBody CreateProviderDTO createProvider) {
        return providerService.createProvider(createProvider);
    }

    @Operation(summary = "Mettre à jour un fournisseur",
              description = "Met à jour les informations d'un fournisseur existant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Fournisseur mis à jour avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "404", description = "Fournisseur non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<Provider>> updateProvider(
        @Parameter(description = "ID du fournisseur à mettre à jour") @PathVariable Long id,
        @Parameter(description = "Nouvelles données du fournisseur") @RequestBody UpdateProviderDTO updateProvider) {
        return providerService.updateProvider(id, updateProvider);
    }

    @Operation(summary = "Supprimer un fournisseur",
              description = "Supprime un fournisseur existant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Fournisseur supprimé avec succès"),
        @ApiResponse(responseCode = "404", description = "Fournisseur non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<Void>> deleteProvider(
        @Parameter(description = "ID du fournisseur à supprimer") @PathVariable Long id) {
        return providerService.deleteProvider(id);
    }
}
