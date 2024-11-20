package com.vlosco.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vlosco.backend.dto.ResponseDTO;
import com.vlosco.backend.dto.VehicleCreationDTO;
import com.vlosco.backend.dto.VehicleUpdateDTO;
import com.vlosco.backend.model.Vehicle;
import com.vlosco.backend.service.VehicleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Contrôleur REST pour la gestion des véhicules.
 * Fournit les endpoints pour les opérations CRUD sur les véhicules.
 */
@Tag(name = "Vehicles", description = "API pour la gestion des véhicules")
@RestController
@RequestMapping("/vehicles")
@CrossOrigin(origins = "*")
public class VehicleController {

    private final VehicleService vehicleService;

    @Autowired
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @Operation(summary = "Récupérer tous les véhicules",
              description = "Retourne la liste complète des véhicules")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des véhicules récupérée avec succès"),
        @ApiResponse(responseCode = "204", description = "Aucun véhicule trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping
    public ResponseEntity<ResponseDTO<List<Vehicle>>> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }

    @Operation(summary = "Récupérer un véhicule par ID",
              description = "Retourne un véhicule spécifique basé sur son ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Véhicule trouvé"),
        @ApiResponse(responseCode = "404", description = "Véhicule non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<Vehicle>> getVehicleById(
        @Parameter(description = "ID du véhicule à récupérer") @PathVariable Long id) {
        return vehicleService.getVehicleById(id);
    }

    @Operation(summary = "Créer un nouveau véhicule",
              description = "Crée un nouveau véhicule avec les informations fournies")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Véhicule créé avec succès"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @PostMapping
    public ResponseEntity<ResponseDTO<Vehicle>> createVehicle(
        @Parameter(description = "Données du véhicule à créer") @RequestBody VehicleCreationDTO vehicleDto) {
        return vehicleService.createVehicle(vehicleDto);
    }

    @Operation(summary = "Mettre à jour un véhicule",
              description = "Met à jour les informations d'un véhicule existant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Véhicule mis à jour avec succès"),
        @ApiResponse(responseCode = "404", description = "Véhicule non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<Vehicle>> updateVehicle(
        @Parameter(description = "ID du véhicule à mettre à jour") @PathVariable Long id,
        @Parameter(description = "Nouvelles données du véhicule") @RequestBody VehicleUpdateDTO vehicleDto) {
        return vehicleService.updateVehicle(id, vehicleDto);
    }

    @Operation(summary = "Supprimer un véhicule",
              description = "Supprime un véhicule existant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Véhicule supprimé avec succès"),
        @ApiResponse(responseCode = "404", description = "Véhicule non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<Void>> deleteVehicle(
        @Parameter(description = "ID du véhicule à supprimer") @PathVariable Long id) {
        return vehicleService.deleteVehicle(id);
    }
}