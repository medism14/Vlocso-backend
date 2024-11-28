package com.vlosco.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.vlosco.backend.dto.ResponseDTO;
import com.vlosco.backend.dto.VehicleCreationDTO;
import com.vlosco.backend.dto.VehicleUpdateDTO;
import com.vlosco.backend.model.Vehicle;
import com.vlosco.backend.repository.VehicleRepository;

/**
 * Service gérant les opérations CRUD pour les véhicules.
 * Fournit des méthodes pour créer, lire, mettre à jour et supprimer des
 * véhicules
 * tout en gérant les réponses HTTP appropriées et la validation des données.
 */
@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Récupère la liste complète des véhicules stockés en base de données.
     * Effectue une validation du résultat et gère les cas d'erreur.
     * 
     * @return ResponseEntity contenant:
     *         - 200 OK: Liste des véhicules avec message de succès
     *         - 204 NO_CONTENT: Si aucun véhicule n'existe
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    public ResponseEntity<ResponseDTO<List<Vehicle>>> getAllVehicles() {
        ResponseDTO<List<Vehicle>> response = new ResponseDTO<>();

        try {
            List<Vehicle> vehicles = vehicleRepository.findAll();
            // Vérifie si la liste est vide et retourne NO_CONTENT le cas échéant
            if (vehicles.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            response.setMessage("La liste des véhicules a bien été récupérée");
            response.setData(vehicles);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Une erreur est survenue lors de la récupération des véhicules");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère un véhicule spécifique par son identifiant unique.
     * Vérifie l'existence du véhicule et gère les cas d'erreur.
     * 
     * @param id Identifiant unique du véhicule à récupérer
     * @return ResponseEntity contenant:
     *         - 200 OK: Le véhicule trouvé avec message de succès
     *         - 404 NOT_FOUND: Si aucun véhicule ne correspond à l'ID
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    public ResponseEntity<ResponseDTO<Vehicle>> getVehicleById(Long id) {
        ResponseDTO<Vehicle> response = new ResponseDTO<>();

        try {
            Optional<Vehicle> vehicleOptional = vehicleRepository.findById(id);

            if (vehicleOptional.isPresent()) {
                response.setData(vehicleOptional.get());
                response.setMessage("Le véhicule a bien été récupéré");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setMessage("Le véhicule n'a pas été trouvé");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Une erreur est survenue lors de la récupération du véhicule");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Crée un nouveau véhicule dans la base de données.
     * Initialise les timestamps de création/modification et valide les données.
     * 
     * @param vehicleDto Objet VehicleCreationDTO contenant les informations du
     *                   nouveau véhicule
     * @return ResponseEntity contenant:
     *         - 201 CREATED: Le véhicule créé avec message de succès
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique ou de
     *         validation
     */
    public ResponseEntity<ResponseDTO<Vehicle>> createVehicle(VehicleCreationDTO vehicleDto) {
        ResponseDTO<Vehicle> response = new ResponseDTO<>();

        try {
            // Création du véhicule
            Vehicle vehicle = new Vehicle();
            vehicle.setMark(vehicleDto.getMark());
            vehicle.setModel(vehicleDto.getModel());
            vehicle.setYear(vehicleDto.getYear());
            vehicle.setGearbox(vehicleDto.getGearbox());
            vehicle.setClimatisation(vehicleDto.getClimatisation());
            vehicle.setFuelType(vehicleDto.getFuelType());
            vehicle.setKlmCounter(vehicleDto.getKlm_counter());
            vehicle.setDescription(vehicleDto.getDescription());
            vehicle.setType(vehicleDto.getType());
            vehicle.setCondition(vehicleDto.getCondition());

            // Initialisation des timestamps de création et mise à jour
            Vehicle savedVehicle = vehicleRepository.save(vehicle);

            response.setData(savedVehicle);
            response.setMessage("Le véhicule a bien été créé");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Une erreur est survenue lors de la création du véhicule");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Met à jour les informations d'un véhicule existant.
     * Vérifie l'existence du véhicule et met à jour tous les champs modifiables.
     * 
     * @param id             Identifiant unique du véhicule à mettre à jour
     * @param updatedVehicle Objet Vehicle contenant les nouvelles données
     * @return ResponseEntity contenant:
     *         - 200 OK: Le véhicule mis à jour avec message de succès
     *         - 404 NOT_FOUND: Si aucun véhicule ne correspond à l'ID
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    public ResponseEntity<ResponseDTO<Vehicle>> updateVehicle(Long id, VehicleUpdateDTO updatedVehicle) {
        ResponseDTO<Vehicle> response = new ResponseDTO<>();

        try {
            Optional<Vehicle> vehicleOptional = vehicleRepository.findById(id);

            if (vehicleOptional.isPresent()) {
                Vehicle vehicle = vehicleOptional.get();
                // Mise à jour de tous les champs modifiables du véhicule
                if (updatedVehicle.getMark() != null)
                    vehicle.setMark(updatedVehicle.getMark());
                if (updatedVehicle.getModel() != null)
                    vehicle.setModel(updatedVehicle.getModel());
                if (updatedVehicle.getYear() != null)
                    vehicle.setYear(updatedVehicle.getYear());
                if (updatedVehicle.getFuelType() != null)
                    vehicle.setFuelType(updatedVehicle.getFuelType());
                if (updatedVehicle.getGearbox() != null)
                    vehicle.setGearbox(updatedVehicle.getGearbox());
                if (updatedVehicle.getKlm_counter() != null)
                    vehicle.setKlmCounter(updatedVehicle.getKlm_counter());
                if (updatedVehicle.getClimatisation() != null)
                    vehicle.setClimatisation(updatedVehicle.getClimatisation());
                if (updatedVehicle.getDescription() != null)
                    vehicle.setDescription(updatedVehicle.getDescription());
                vehicle.setType(vehicle.getType());
                vehicle.setCondition(updatedVehicle.getCondition());
                vehicle.setUpdatedAt(LocalDateTime.now());

                Vehicle savedVehicle = vehicleRepository.save(vehicle);
                response.setData(savedVehicle);
                response.setMessage("Le véhicule a bien été mis à jour");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setMessage("Le véhicule n'a pas été trouvé");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Une erreur est survenue lors de la mise à jour du véhicule");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Supprime définitivement un véhicule de la base de données.
     * Vérifie l'existence du véhicule avant la suppression.
     * 
     * @param id Identifiant unique du véhicule à supprimer
     * @return ResponseEntity contenant:
     *         - 200 OK: Message de confirmation si suppression réussie
     *         - 404 NOT_FOUND: Si aucun véhicule ne correspond à l'ID
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    public ResponseEntity<ResponseDTO<Void>> deleteVehicle(Long id) {
        ResponseDTO<Void> response = new ResponseDTO<>();

        try {
            // Vérifie l'existence du véhicule avant tentative de suppression
            if (vehicleRepository.existsById(id)) {
                vehicleRepository.deleteById(id);
                response.setMessage("Le véhicule a bien été supprimé");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setMessage("Le véhicule n'a pas été trouvé");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Une erreur est survenue lors de la suppression du véhicule");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
