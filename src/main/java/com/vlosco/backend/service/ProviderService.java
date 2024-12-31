package com.vlosco.backend.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vlosco.backend.dto.CreateProviderDTO;
import com.vlosco.backend.dto.ResponseDTO;
import com.vlosco.backend.dto.UpdateProviderDTO;
import com.vlosco.backend.model.Provider;
import com.vlosco.backend.repository.ProviderRepository;
import java.util.List;

/**
 * Service gérant les opérations CRUD pour les fournisseurs (providers).
 * Permet de créer, lire, mettre à jour et supprimer des providers tout en gérant
 * les réponses HTTP appropriées et la validation des données.
 */
@Service
public class ProviderService {
    private final ProviderRepository providerRepository;

    @Autowired
    public ProviderService(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    /**
     * Récupère la liste complète des providers depuis la base de données.
     * Cette méthode permet d'obtenir tous les providers enregistrés sans filtrage.
     * 
     * @return ResponseEntity contenant:
     *         - 200 OK: Liste des providers avec message de succès
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique lors de la récupération
     */
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseDTO<List<Provider>>> getAllProviders() {
        ResponseDTO<List<Provider>> response = new ResponseDTO<>();
        try {
            List<Provider> providers = providerRepository.findAll();
            response.setMessage("La listes de providers ont bien été récupérés");
            response.setData(providers);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Erreur lors de la récupérations des providers");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère un provider spécifique par son identifiant unique.
     * Vérifie l'existence du provider et retourne les informations détaillées le concernant.
     * 
     * @param id L'identifiant unique du provider à rechercher dans la base de données
     * @return ResponseEntity contenant:
     *         - 200 OK: Le provider trouvé avec message de succès
     *         - 404 NOT_FOUND: Si aucun provider ne correspond à l'ID fourni
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique lors de la recherche
     */
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseDTO<Provider>> getProviderById(Long id) {
        ResponseDTO<Provider> response = new ResponseDTO<>();
        try {
            Optional<Provider> providerOptional = providerRepository.findById(id);

            if (providerOptional.isPresent()) {
                response.setMessage("Le provider a bien été récupéré");
                response.setData(providerOptional.get());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setMessage("Le provider n'a pas été trouvé");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Erreur lors de la récupération du provider");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Crée un nouveau provider dans la base de données.
     * Valide les données d'entrée avant la création et configure le nouveau provider.
     * 
     * @param providerCreate DTO contenant les informations nécessaires pour créer un provider,
     *                      notamment le nom du provider
     * @return ResponseEntity contenant:
     *         - 201 CREATED: Le provider créé avec message de succès
     *         - 400 BAD_REQUEST: Si les données fournies sont invalides ou manquantes
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique lors de la création
     */
    @Transactional
    public ResponseEntity<ResponseDTO<Provider>> createProvider(CreateProviderDTO providerCreate) {
        ResponseDTO<Provider> response = new ResponseDTO<>();
        // Validation des données d'entrée
        if (providerCreate == null) {
            response.setMessage("Les données du provider sont invalides");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            // Création et configuration du nouveau provider
            Provider provider = new Provider();
            provider.setProviderName(providerCreate.getProviderName());

            Provider savedProvider = providerRepository.save(provider);
            response.setMessage("Le provider a bien été créé");
            response.setData(savedProvider);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Erreur lors de la création du provider");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Met à jour les informations d'un provider existant.
     * Vérifie l'existence du provider et valide les nouvelles données avant la mise à jour.
     * 
     * @param id Identifiant unique du provider à mettre à jour
     * @param providerUpdate DTO contenant les nouvelles informations à appliquer au provider
     * @return ResponseEntity contenant:
     *         - 200 OK: Le provider mis à jour avec message de succès
     *         - 400 BAD_REQUEST: Si les données de mise à jour sont invalides
     *         - 404 NOT_FOUND: Si le provider à mettre à jour n'existe pas
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique lors de la mise à jour
     */
    @Transactional
    public ResponseEntity<ResponseDTO<Provider>> updateProvider(Long id, UpdateProviderDTO providerUpdate) {
        ResponseDTO<Provider> response = new ResponseDTO<>();
        // Validation des données d'entrée
        if (providerUpdate == null) {
            response.setMessage("Les données du provider sont invalides");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            Optional<Provider> providerOptional = providerRepository.findById(id);

            if (providerOptional.isPresent()) {
                Provider provider = providerOptional.get();
                provider.setProviderName(providerUpdate.getProviderName());
                providerRepository.save(provider);
                response.setMessage("Le provider a bien été mis à jour");
                response.setData(provider);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setMessage("Le provider n'a pas été trouvé");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Erreur lors de la mise à jour du provider");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Supprime un provider de la base de données.
     * Vérifie l'existence du provider avant de procéder à sa suppression.
     * 
     * @param id Identifiant unique du provider à supprimer
     * @return ResponseEntity contenant:
     *         - 200 OK: Message de confirmation si la suppression est réussie
     *         - 404 NOT_FOUND: Si le provider à supprimer n'existe pas
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique lors de la suppression
     */
    @Transactional
    public ResponseEntity<ResponseDTO<Void>> deleteProvider(Long id) {
        ResponseDTO<Void> response = new ResponseDTO<>();
        try {
            Optional<Provider> providerOptional = providerRepository.findById(id);

            if (providerOptional.isPresent()) {
                Provider provider = providerOptional.get();
                providerRepository.delete(provider);
                response.setMessage("Le provider a bien été supprimé");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setMessage("Le provider n'a pas été trouvé");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Erreur lors de la suppression du provider");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
