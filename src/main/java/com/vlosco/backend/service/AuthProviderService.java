package com.vlosco.backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vlosco.backend.dto.CreateAuthProviderDTO;
import com.vlosco.backend.dto.ResponseDTO;
import com.vlosco.backend.dto.UpdateAuthProviderDTO;
import com.vlosco.backend.model.AuthProvider;
import com.vlosco.backend.model.Provider;
import com.vlosco.backend.model.User;
import com.vlosco.backend.repository.AuthProviderRepository;
import com.vlosco.backend.repository.ProviderRepository;
import com.vlosco.backend.repository.UserRepository;

import java.util.List;
import java.util.Optional;

/**
 * Service gérant les fournisseurs d'authentification (AuthProvider).
 * Permet de gérer les liens entre les utilisateurs et leurs différents moyens d'authentification
 * (ex: Google, Facebook, etc.).
 * Fournit des opérations CRUD et des fonctionnalités de recherche pour les AuthProviders.
 */
@Service
public class AuthProviderService {

    private final AuthProviderRepository authProviderRepository;
    private final ProviderRepository providerRepository;
    private final UserRepository userRepository;

    /**
     * Constructeur du service AuthProvider.
     * Initialise les repositories nécessaires via injection de dépendances.
     *
     * @param authProviderRepository Repository pour la gestion des AuthProviders
     * @param providerRepository Repository pour la gestion des Providers
     * @param userRepository Repository pour la gestion des Users
     */
    public AuthProviderService(AuthProviderRepository authProviderRepository, ProviderRepository providerRepository,
            UserRepository userRepository) {
        this.authProviderRepository = authProviderRepository;
        this.providerRepository = providerRepository;
        this.userRepository = userRepository;
    }

    /**
     * Récupère la liste complète des AuthProviders.
     * 
     * @return ResponseEntity contenant:
     *         - 200 OK: Liste des AuthProviders si la récupération est réussie
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseDTO<List<AuthProvider>>> getAllAuthProviders() {
        ResponseDTO<List<AuthProvider>> response = new ResponseDTO<>();
        try {
            List<AuthProvider> authProviders = authProviderRepository.findAll();
            response.setMessage("L'auth providers ont bien été récupérés");
            response.setData(authProviders);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Erreur lors de la récupération des auth providers");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère un AuthProvider spécifique par son identifiant.
     * 
     * @param id Identifiant unique de l'AuthProvider à récupérer
     * @return ResponseEntity contenant:
     *         - 200 OK: AuthProvider trouvé
     *         - 404 NOT_FOUND: Si l'AuthProvider n'existe pas
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseDTO<AuthProvider>> getAuthProvider(Long id) {
        ResponseDTO<AuthProvider> response = new ResponseDTO<>();
        try {
            Optional<AuthProvider> authProvider = authProviderRepository.findById(id);

            if (authProvider.isPresent()) {
                response.setMessage("L'auth provider a bien été récupéré");
                response.setData(authProvider.get());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setMessage("L'auth provider n'a pas été trouvé");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Erreur lors de la récupération de l'auth provider");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Crée un nouvel AuthProvider avec les informations fournies.
     * 
     * @param createAuthProvider DTO contenant les informations nécessaires (providerId, userId, accountProviderId)
     * @return ResponseEntity contenant:
     *         - 201 CREATED: AuthProvider créé avec succès
     *         - 400 BAD_REQUEST: Si les données sont invalides ou si le provider/user n'existe pas
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    @Transactional
    public ResponseEntity<ResponseDTO<AuthProvider>> createAuthProvider(CreateAuthProviderDTO createAuthProvider) {
        ResponseDTO<AuthProvider> response = new ResponseDTO<>();
        // Validation des données d'entrée
        if (createAuthProvider == null) {
            response.setMessage("Les données de l'auth provider sont invalides");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            AuthProvider authProvider = new AuthProvider();
            // Récupération du provider et de l'utilisateur associés
            Optional<Provider> provider = providerRepository.findById(createAuthProvider.getProviderId());
            Optional<User> user = userRepository.findById(createAuthProvider.getUserId());

            if (provider.isPresent() && user.isPresent()) {
                // Configuration de l'AuthProvider avec les données fournies
                authProvider.setProvider(provider.get());
                authProvider.setUser(user.get());
                authProvider.setAccountProviderId(createAuthProvider.getAccountProviderId());
                AuthProvider savedAuthProvider = authProviderRepository.save(authProvider);
                response.setMessage("L'auth provider a bien été créé");
                response.setData(savedAuthProvider);
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } else {
                response.setMessage("Le provider ou l'utilisateur n'existe pas");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Erreur lors de la création de l'auth provider");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Met à jour un AuthProvider existant avec les nouvelles informations fournies.
     * 
     * @param id Identifiant de l'AuthProvider à mettre à jour
     * @param updateAuthProvider DTO contenant les nouvelles informations (providerId, userId, accountProviderId)
     * @return ResponseEntity contenant:
     *         - 200 OK: AuthProvider mis à jour avec succès
     *         - 400 BAD_REQUEST: Si les données sont invalides ou si le provider/user n'existe pas
     *         - 404 NOT_FOUND: Si l'AuthProvider à mettre à jour n'existe pas
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    @Transactional
    public ResponseEntity<ResponseDTO<AuthProvider>> updateAuthProvider(Long id, UpdateAuthProviderDTO updateAuthProvider) {
        ResponseDTO<AuthProvider> response = new ResponseDTO<>();
        // Validation des données d'entrée
        if (updateAuthProvider == null) {
            response.setMessage("Les données de l'auth provider sont invalides");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            Optional<AuthProvider> authProviderOptional = authProviderRepository.findById(id);

            if (authProviderOptional.isEmpty()) {
                response.setMessage("L'auth provider n'a pas été trouvé");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            AuthProvider authProvider = authProviderOptional.get();

            // Mise à jour du provider si spécifié
            if (updateAuthProvider.getProviderId() != null) {
                Optional<Provider> provider = providerRepository.findById(updateAuthProvider.getProviderId());

                if (provider.isPresent()) {
                    authProvider.setProvider(provider.get());
                } else {
                    response.setMessage("Le provider n'existe pas");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
            }

            // Mise à jour de l'utilisateur si spécifié
            if (updateAuthProvider.getUserId() != null) {
                Optional<User> user = userRepository.findById(updateAuthProvider.getUserId());

                if (user.isPresent()) {
                    authProvider.setUser(user.get());
                } else {
                    response.setMessage("L'utilisateur n'existe pas");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
            }

            // Mise à jour de l'ID du compte provider si spécifié
            if (updateAuthProvider.getAccountProviderId() != null) {
                authProvider.setAccountProviderId(updateAuthProvider.getAccountProviderId());
            }

            AuthProvider updatedAuthProvider = authProviderRepository.save(authProvider);
            response.setMessage("L'auth provider a bien été mis à jour");
            response.setData(updatedAuthProvider);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Erreur lors de la mise à jour de l'auth provider");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Supprime un AuthProvider existant.
     * 
     * @param id Identifiant de l'AuthProvider à supprimer
     * @return ResponseEntity contenant:
     *         - 200 OK: AuthProvider supprimé avec succès
     *         - 404 NOT_FOUND: Si l'AuthProvider n'existe pas
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    @Transactional
    public ResponseEntity<ResponseDTO<Void>> deleteAuthProvider(Long id) {
        ResponseDTO<Void> response = new ResponseDTO<>();
        try {
            Optional<AuthProvider> authProvider = authProviderRepository.findById(id);

            if (authProvider.isPresent()) {
                authProviderRepository.delete(authProvider.get());
                response.setMessage("L'auth provider a bien été supprimé");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setMessage("L'auth provider n'a pas été trouvé");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Erreur lors de la suppression de l'auth provider");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère tous les utilisateurs qui ont au moins un AuthProvider associé.
     * 
     * @return ResponseEntity contenant:
     *         - 200 OK: Liste des utilisateurs avec leurs AuthProviders
     *         - 404 NOT_FOUND: Si aucun utilisateur n'a d'AuthProvider
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseDTO<List<User>>> getAllUsersWithAuthProviders() {
        ResponseDTO<List<User>> response = new ResponseDTO<>();
        try {
            Optional<List<User>> users = authProviderRepository.findAllUsersWithProviders();

            if (users.isPresent()) {
                response.setMessage("La liste des utilisateurs avec auth providers a bien été récupérée");
                response.setData(users.get());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setMessage("Aucun utilisateur avec auth provider n'a été trouvé");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Erreur lors de la récupération des utilisateurs avec auth providers");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère un utilisateur spécifique avec ses AuthProviders associés.
     * 
     * @param email Email de l'utilisateur à rechercher
     * @return ResponseEntity contenant:
     *         - 200 OK: Utilisateur trouvé avec ses AuthProviders
     *         - 404 NOT_FOUND: Si l'utilisateur n'existe pas ou n'a pas d'AuthProvider
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseDTO<User>> getUserWithAuthProvider(String email) {
        ResponseDTO<User> response = new ResponseDTO<>();
        try {
            Optional<User> user = authProviderRepository.findUserWithEmailAndIsActive(email);

            if (user.isPresent()) {
                response.setMessage("L'utilisateur avec auth provider a bien été récupéré");
                response.setData(user.get());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setMessage("Aucun utilisateur avec auth provider n'a été trouvé");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Erreur lors de la récupération de l'utilisateur avec auth provider");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
