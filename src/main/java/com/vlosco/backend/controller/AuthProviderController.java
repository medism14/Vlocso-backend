package com.vlosco.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;

import com.vlosco.backend.dto.CreateAuthProviderDTO;
import com.vlosco.backend.dto.ResponseDTO;
import com.vlosco.backend.dto.UpdateAuthProviderDTO;
import com.vlosco.backend.model.AuthProvider;
import com.vlosco.backend.model.User;
import com.vlosco.backend.service.AuthProviderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Contrôleur REST gérant les opérations CRUD pour les fournisseurs d'authentification
 * et les relations avec les utilisateurs.
 * Permet la gestion complète des AuthProviders via des endpoints REST.
 */
@Tag(name = "Auth Providers", description = "API pour la gestion des fournisseurs d'authentification")
@RestController
@RequestMapping("/auth_providers")
@CrossOrigin(origins = "*")
public class AuthProviderController {

    public final AuthProviderService authProviderService;

    @Autowired
    public AuthProviderController(AuthProviderService authProviderService) {
        this.authProviderService = authProviderService;
    }

    @Operation(summary = "Récupérer tous les fournisseurs d'authentification",
              description = "Retourne la liste complète des AuthProviders")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des AuthProviders récupérée avec succès"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping
    public ResponseEntity<ResponseDTO<List<AuthProvider>>> getAllAuthProviders() {
        return authProviderService.getAllAuthProviders();
    }

    @Operation(summary = "Récupérer un fournisseur d'authentification par ID",
              description = "Retourne un AuthProvider spécifique basé sur son ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "AuthProvider trouvé"),
        @ApiResponse(responseCode = "404", description = "AuthProvider non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<AuthProvider>> getAuthProvider(
        @Parameter(description = "ID de l'AuthProvider à récupérer") @PathVariable Long id) {
        return authProviderService.getAuthProvider(id);
    }

    @Operation(summary = "Créer un nouveau fournisseur d'authentification",
              description = "Crée un nouvel AuthProvider avec les informations fournies")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "AuthProvider créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @PostMapping
    public ResponseEntity<ResponseDTO<AuthProvider>> createAuthProvider(
        @Parameter(description = "Données de création de l'AuthProvider") @RequestBody CreateAuthProviderDTO createAuthProvider) {
        return authProviderService.createAuthProvider(createAuthProvider);
    }

    @Operation(summary = "Mettre à jour un fournisseur d'authentification",
              description = "Met à jour un AuthProvider existant avec les nouvelles informations")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "AuthProvider mis à jour avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "404", description = "AuthProvider non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<AuthProvider>> updateAuthProvider(
        @Parameter(description = "ID de l'AuthProvider à mettre à jour") @PathVariable Long id,
        @Parameter(description = "Nouvelles données de l'AuthProvider") @RequestBody UpdateAuthProviderDTO updateAuthProvider) {
        return authProviderService.updateAuthProvider(id, updateAuthProvider);
    }

    @Operation(summary = "Supprimer un fournisseur d'authentification",
              description = "Supprime un AuthProvider existant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "AuthProvider supprimé avec succès"),
        @ApiResponse(responseCode = "404", description = "AuthProvider non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<Void>> deleteAuthProvider(
        @Parameter(description = "ID de l'AuthProvider à supprimer") @PathVariable Long id) {
        return authProviderService.deleteAuthProvider(id);
    }

    @Operation(summary = "Récupérer tous les utilisateurs avec leurs fournisseurs d'authentification",
              description = "Retourne la liste des utilisateurs ayant au moins un AuthProvider")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des utilisateurs récupérée avec succès"),
        @ApiResponse(responseCode = "404", description = "Aucun utilisateur trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping("/users-provider")
    public ResponseEntity<ResponseDTO<List<User>>> getAllUsersWithAuthProviders() {
        return authProviderService.getAllUsersWithAuthProviders();
    }

    @Operation(summary = "Rechercher un utilisateur avec ses fournisseurs d'authentification",
              description = "Retourne un utilisateur spécifique avec ses AuthProviders associés")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Utilisateur trouvé avec ses AuthProviders"),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping("/find-user/{email}")
    public ResponseEntity<ResponseDTO<User>> getUserWithAuthProvider(
        @Parameter(description = "Email de l'utilisateur à rechercher") @PathVariable String email) {
        return authProviderService.getUserWithAuthProvider(email);
    }
}
