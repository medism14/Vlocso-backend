package com.vlosco.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vlosco.backend.dto.ResponseDTO;
import com.vlosco.backend.dto.UserUpdateDTO;
import com.vlosco.backend.dto.UserUpdatePasswordDto;
import com.vlosco.backend.model.User;
import com.vlosco.backend.repository.UserRepository;

/**
 * Service responsable de la gestion des utilisateurs dans l'application.
 * Gère toutes les opérations CRUD sur les utilisateurs ainsi que les processus métier comme :
 * - La vérification des emails
 * - La réinitialisation des mots de passe 
 * - La mise à jour des profils
 * - La désactivation des comptes
 */
@Service
public class UserService {

    // Repositories et services nécessaires pour la gestion des utilisateurs
    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final EmailService emailService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordService passwordService, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.emailService = emailService;
    }   

    /**
     * Récupère la liste de tous les utilisateurs actifs dans le système.
     * Un utilisateur est considéré comme actif si son attribut isActive est true.
     * 
     * @return ResponseEntity contenant:
     *         - 200 OK: Liste des utilisateurs actifs avec message de succès
     *         - 204 NO_CONTENT: Si aucun utilisateur actif n'est trouvé
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseDTO<List<User>>> getAllUsers() {
        ResponseDTO<List<User>> response = new ResponseDTO<>();

        try {
            // Récupération des utilisateurs actifs uniquement
            List<User> users = userRepository.findByIsActiveTrue();
            if (users.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            response.setMessage("La liste des utilisateurs ont bien été récupérés");
            response.setData(users);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Une erreur est survenue lors de la récupération des utilisateurs");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Recherche et retourne un utilisateur spécifique par son identifiant.
     * 
     * @param id L'identifiant unique de l'utilisateur à rechercher
     * @return ResponseEntity contenant:
     *         - 200 OK: L'utilisateur trouvé avec message de succès
     *         - 404 NOT_FOUND: Si l'utilisateur n'existe pas
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseDTO<User>> getUserById(Long id) {
        ResponseDTO<User> response = new ResponseDTO<>();

        try {
            Optional<User> userOptional = userRepository.findById(id);

            if (userOptional.isPresent()) {
                response.setData(userOptional.get());
                response.setMessage("L'utilisateur a bien été récupéré");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setMessage("L'utilisateur n'a pas été trouvé");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Une erreur est survenue lors de la récupération de l'utilisateur");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Recherche un utilisateur actif par son adresse email.
     * Cette méthode est principalement utilisée dans le processus d'authentification.
     * 
     * @param email L'adresse email de l'utilisateur à rechercher
     * @return ResponseEntity contenant:
     *         - 200 OK: L'utilisateur trouvé avec message de succès
     *         - 404 NOT_FOUND: Si aucun utilisateur actif n'est trouvé avec cet email
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseDTO<User>> getUserByEmail(String email) {
        ResponseDTO<User> response = new ResponseDTO<>();

        try {
            // Recherche uniquement parmi les utilisateurs actifs
            Optional<User> userOptional = userRepository.findByEmailAndIsActiveTrue(email);

            if (userOptional.isPresent()) {
                response.setData(userOptional.get());
                response.setMessage("L'utilisateur a bien été récupéré");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setMessage("L'utilisateur n'a pas été trouvé");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Une erreur est survenue lors de la récupération de l'utilisateur");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Initie le processus de vérification d'email pour un utilisateur.
     * Génère un token unique valide pendant 7 jours et envoie un email de vérification.
     * 
     * @param email L'adresse email à vérifier
     * @return ResponseEntity contenant:
     *         - 200 OK: Si l'email de vérification a été envoyé avec succès
     *         - 404 NOT_FOUND: Si aucun utilisateur actif n'est trouvé avec cet email
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    @Transactional
    public ResponseEntity<ResponseDTO<Void>> launchVerifEmail(String email) {
        ResponseDTO<Void> response = new ResponseDTO<>();

        try {
            Optional<User> userOptional = userRepository.findByEmailAndIsActiveTrue(email);

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // Génération d'un nouveau token de vérification avec une validité de 7 jours
                String verificationToken = getUniqueToken();
                user.setEmailVerificationToken(verificationToken);
                user.setEmailVerificationTokenExpiration(LocalDateTime.now().plusDays(7));
                userRepository.save(user);
                
                // Envoi de l'email de vérification via le service dédié
                emailService.sendVerificationEmail(user.getEmail(), verificationToken);

                response.setMessage("L'email de vérification a bien été envoyé");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setMessage("L'utilisateur n'a pas été trouvé");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Une erreur est survenue lors de l'envoi de l'email de vérification");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Initie le processus de réinitialisation de mot de passe.
     * Génère un token unique valide pendant 7 jours et envoie un email avec les instructions.
     * 
     * @param email L'adresse email de l'utilisateur demandant la réinitialisation
     * @return ResponseEntity contenant:
     *         - 200 OK: Si l'email de réinitialisation a été envoyé avec succès
     *         - 404 NOT_FOUND: Si aucun utilisateur actif n'est trouvé avec cet email
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    @Transactional
    public ResponseEntity<ResponseDTO<Void>> launchUpdatePassword(String email) {
        ResponseDTO<Void> response = new ResponseDTO<>();

        try {
            Optional<User> userOptional = userRepository.findByEmailAndIsActiveTrue(email);

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // Génération du token de réinitialisation avec une validité de 7 jours
                String verificationToken = getUniqueToken();
                user.setPasswordVerificationToken(verificationToken);
                user.setPasswordVerificationTokenExpiration(LocalDateTime.now().plusDays(7));
                userRepository.save(user);
                
                // Envoi de l'email de réinitialisation via le service dédié
                emailService.sendUpdatePassword(user.getEmail(), verificationToken);

                response.setMessage("L'email de réinitialisation du mot de passe a bien été envoyé");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setMessage("L'utilisateur n'a pas été trouvé");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Une erreur est survenue lors de l'envoi de l'email de réinitialisation");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Vérifie la validité d'un token de réinitialisation de mot de passe.
     * Contrôle l'existence du token et sa date d'expiration.
     * 
     * @param passwordToken Le token de réinitialisation à vérifier
     * @return ResponseEntity contenant:
     *         - 200 OK: L'utilisateur associé au token si celui-ci est valide
     *         - 400 BAD_REQUEST: Si le token a expiré
     *         - 404 NOT_FOUND: Si le token n'existe pas
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseDTO<User>> verifUpdatePassword(String passwordToken) {
        ResponseDTO<User> response = new ResponseDTO<>();

        try {
            Optional<User> userOptional = userRepository.findByPasswordVerificationToken(passwordToken);

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // Vérification de la validité temporelle du token
                if (user.getPasswordVerificationTokenExpiration().isBefore(LocalDateTime.now())) {
                    response.setMessage("Le token de réinitialisation a expiré");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }

                response.setData(user);
                response.setMessage("Le token de réinitialisation est valide");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setMessage("Token de réinitialisation invalide");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Une erreur est survenue lors de la vérification du token");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Met à jour le mot de passe d'un utilisateur et réinitialise les tokens associés.
     * 
     * @param userUpdatePassword DTO contenant l'ID de l'utilisateur et le nouveau mot de passe
     * @return ResponseEntity contenant:
     *         - 200 OK: Si le mot de passe a été mis à jour avec succès
     *         - 404 NOT_FOUND: Si l'utilisateur n'existe pas
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    @Transactional
    public ResponseEntity<ResponseDTO<Void>> updatePassword(UserUpdatePasswordDto userUpdatePassword) {
        ResponseDTO<Void> response = new ResponseDTO<>();

        try {
            Optional<User> userOptional = userRepository.findById(userUpdatePassword.getId());

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                
                // Mise à jour du mot de passe et nettoyage des tokens de vérification
                user.setPassword(passwordService.hashPassword(userUpdatePassword.getPassword()));
                user.setPasswordVerificationToken(null);
                user.setPasswordVerificationTokenExpiration(null);
                userRepository.save(user);

                response.setMessage("Le mot de passe a bien été mis à jour");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setMessage("L'utilisateur n'a pas été trouvé");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Une erreur est survenue lors de la mise à jour du mot de passe");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Valide l'adresse email d'un utilisateur via le token reçu.
     * Vérifie la validité du token et met à jour le statut de vérification de l'email.
     * 
     * @param emailToken Token de vérification reçu par email
     * @return ResponseEntity contenant:
     *         - 200 OK: Si l'email a été vérifié avec succès
     *         - 400 BAD_REQUEST: Si l'email est déjà vérifié ou si le token a expiré
     *         - 404 NOT_FOUND: Si le token n'existe pas
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    @Transactional
    public ResponseEntity<String> verifyEmail(String emailToken) {
        try {
            Optional<User> userOptional = userRepository.findByEmailVerificationToken(emailToken);

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // Vérifications de validité du token et du statut de l'email
                if (user.getEmailVerified()) {
                    return new ResponseEntity<>("Votre email a déjà été vérifié", HttpStatus.BAD_REQUEST);
                }

                if (user.getEmailVerificationTokenExpiration().isBefore(LocalDateTime.now())) {
                    return new ResponseEntity<>("La vérification de votre email a expiré", HttpStatus.BAD_REQUEST);
                }

                // Validation de l'email et nettoyage des tokens de vérification
                user.setEmailVerified(true);
                user.setEmailVerificationToken(null);
                user.setEmailVerificationTokenExpiration(null);
                userRepository.save(user);
                return new ResponseEntity<>("Votre email a bien été vérifié", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("L'utilisateur n'a pas été trouvé", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Erreur lors de la validation des données", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Met à jour les informations du profil d'un utilisateur.
     * Permet la mise à jour sélective des champs fournis dans le DTO.
     * 
     * @param id ID de l'utilisateur à mettre à jour
     * @param userDetails DTO contenant les nouvelles informations (seuls les champs non-null seront mis à jour)
     * @return ResponseEntity contenant:
     *         - 200 OK: L'utilisateur mis à jour avec message de succès
     *         - 404 NOT_FOUND: Si l'utilisateur n'existe pas
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    @Transactional
    public ResponseEntity<ResponseDTO<User>> updateUser(Long id, UserUpdateDTO userDetails) {
        ResponseDTO<User> response = new ResponseDTO<>();

        try {
            Optional<User> userOptional = userRepository.findById(id);

            if (userOptional.isPresent()) {
                User existingUser = userOptional.get();

                // Mise à jour conditionnelle des champs non-null uniquement
                if (userDetails.getFirstName() != null) {
                    existingUser.setFirstName(userDetails.getFirstName());
                }
                if (userDetails.getLastName() != null) {
                    existingUser.setLastName(userDetails.getLastName());
                }
                if (userDetails.getEmail() != null) {
                    existingUser.setEmail(userDetails.getEmail());
                }
                if (userDetails.getPassword() != null) {
                    existingUser.setPassword(passwordService.hashPassword(userDetails.getPassword()));
                }
                if (userDetails.getPhoneNumber() != null) {
                    existingUser.setPhoneNumber(userDetails.getPhoneNumber());
                }
                if (userDetails.getCountry() != null) {
                    existingUser.setCountry(userDetails.getCountry());
                }
                if (userDetails.getCity() != null) {
                    existingUser.setCity(userDetails.getCity());
                }
                if (userDetails.getUrlImageUser() != null) {
                    // Cas spécial pour la suppression d'image de profil
                    if (userDetails.getUrlImageUser().equals("remove")) {
                        existingUser.setUrlImageUser(null);
                    } else {
                        existingUser.setUrlImageUser(userDetails.getUrlImageUser());
                    }
                }

                // Mise à jour de la date de modification
                existingUser.setUpdatedAt(LocalDateTime.now());
                userRepository.save(existingUser);

                response.setData(existingUser);
                response.setMessage("L'utilisateur a bien été mis à jour");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setMessage("L'utilisateur n'a pas été trouvé");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Une erreur est survenue lors de la mise à jour de l'utilisateur");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Désactive le compte d'un utilisateur (soft delete).
     * Le compte n'est pas supprimé physiquement mais marqué comme inactif.
     * 
     * @param id ID de l'utilisateur à désactiver
     * @return ResponseEntity contenant:
     *         - 200 OK: Si l'utilisateur a été désactivé avec succès
     *         - 404 NOT_FOUND: Si l'utilisateur n'existe pas
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    @Transactional
    public ResponseEntity<ResponseDTO<Void>> removeUser(Long id) {
        ResponseDTO<Void> response = new ResponseDTO<>();

        try {
            Optional<User> userOptional = userRepository.findById(id);

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setIsActive(false);
                userRepository.save(user);
                response.setMessage("L'utilisateur a bien été désactivé");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setMessage("L'utilisateur n'a pas été trouvé");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Une erreur est survenue lors de la désactivation de l'utilisateur");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Génère un token UUID unique pour les processus de vérification.
     * Utilisé pour la vérification d'email et la réinitialisation de mot de passe.
     * 
     * @return String contenant un UUID unique généré aléatoirement
     */
    public String getUniqueToken() {
        return UUID.randomUUID().toString();
    }

}
