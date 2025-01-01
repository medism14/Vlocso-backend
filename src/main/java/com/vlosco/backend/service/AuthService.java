package com.vlosco.backend.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vlosco.backend.dto.CreateAuthProviderDTO;
import com.vlosco.backend.dto.ResponseDTO;
import com.vlosco.backend.dto.TokenDTO;
import com.vlosco.backend.dto.UserLoginDTO;
import com.vlosco.backend.dto.UserLoginProviderDTO;
import com.vlosco.backend.dto.UserRegistrationProviderDTO;
import com.vlosco.backend.enums.UserRole;
import com.vlosco.backend.enums.UserType;
import com.vlosco.backend.dto.AuthResponse;
import com.vlosco.backend.dto.UserRegistrationDTO;
import com.vlosco.backend.model.AuthProvider;
import com.vlosco.backend.model.Provider;
import com.vlosco.backend.model.RefreshTokensBlacklist;
import com.vlosco.backend.model.User;
import com.vlosco.backend.repository.ProviderRepository;
import com.vlosco.backend.repository.RefreshTokensBlacklistRepository;
import com.vlosco.backend.repository.UserRepository;

/**
 * Service gérant l'authentification et l'inscription des utilisateurs.
 * Fournit des méthodes pour l'inscription classique, la connexion,
 * et l'authentification via providers externes (Google, Facebook, etc.).
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final RefreshTokensBlacklistRepository refreshTokenBlacklistRepository;
    private final ProviderRepository providerRepository;
    private final AuthProviderService authProviderService;

    /**
     * Constructeur du service d'authentification.
     * Initialise les dépendances nécessaires via injection.
     *
     * @param userRepository                  Repository pour la gestion des
     *                                        utilisateurs
     * @param passwordService                 Service de gestion des mots de passe
     * @param jwtService                      Service de gestion des tokens JWT
     * @param emailService                    Service d'envoi d'emails
     * @param refreshTokenBlacklistRepository Repository pour la gestion de la liste
     *                                        noire des tokens
     * @param providerRepository              Repository pour la gestion des
     *                                        providers d'authentification
     * @param authProviderService             Service de gestion des providers
     *                                        d'authentification
     */
    @Autowired
    public AuthService(UserRepository userRepository, PasswordService passwordService, JwtService jwtService,
            EmailService emailService, RefreshTokensBlacklistRepository refreshTokenBlacklistRepository,
            ProviderRepository providerRepository, AuthProviderService authProviderService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.refreshTokenBlacklistRepository = refreshTokenBlacklistRepository;
        this.providerRepository = providerRepository;
        this.authProviderService = authProviderService;
    }

    /**
     * Enregistre un nouvel utilisateur ou réactive un compte existant.
     * Gère la validation des données, le hashage du mot de passe et l'envoi d'email
     * de vérification.
     *
     * @param userRegister DTO contenant les informations d'inscription (nom,
     *                     prénom, email, etc.)
     * @return ResponseEntity contenant :
     *         - 200 OK : Inscription réussie avec tokens d'authentification
     *         - 400 BAD_REQUEST : Données d'inscription invalides ou incomplètes
     *         - 409 CONFLICT : Un compte actif existe déjà avec cet email
     *         - 500 INTERNAL_SERVER_ERROR : Erreur technique lors du traitement
     */
    @Transactional
    public ResponseEntity<ResponseDTO<Object>> registerUser(UserRegistrationDTO userRegister) {
        // Validation des données d'entrée
        if (userRegister == null || userRegister.getEmail() == null) {
            return new ResponseEntity<>(
                    new ResponseDTO<>(null, "Les informations d'inscription sont incomplètes"),
                    HttpStatus.BAD_REQUEST);
        }

        try {
            User user;
            // Vérifie si l'email existe déjà
            Optional<User> userOptional = userRepository.findByEmail(userRegister.getEmail());

            if (userOptional.isPresent()) {
                user = userOptional.get();
                // Réactive le compte si inactif, sinon retourne une erreur
                if (!user.getIsActive()) {
                    user.setIsActive(true);
                } else {
                    return new ResponseEntity<>(
                            new ResponseDTO<>(null, "Un compte existe déjà avec cet email"),
                            HttpStatus.CONFLICT);
                }
            } else {
                user = new User();
            }

            // Mise à jour des informations utilisateur
            user.setFirstName(userRegister.getFirstName());
            user.setLastName(userRegister.getLastName());
            user.setEmail(userRegister.getEmail());
            user.setPassword(
                    userRegister.getPassword() != null ? passwordService.hashPassword(userRegister.getPassword())
                            : null);
            user.setPhoneNumber(userRegister.getPhoneNumber());
            user.setCity(userRegister.getCity());
            user.setBirthDate(userRegister.getBirthDate());
            user.setRole(userRegister.getRole() != null ? userRegister.getRole() : UserRole.USER);
            user.setType(userRegister.getType() != null ? userRegister.getType() : UserType.REGULAR);
            user.setEmailVerified(userRegister.isEmailVerified());
            user.setUrlImageUser(userRegister.getUrlImageUser());

            userRepository.save(user);

            // Gestion de la vérification email si nécessaire
            if (!userRegister.isEmailVerified()) {
                String verificationToken = UUID.randomUUID().toString();
                user.setEmailVerificationToken(verificationToken);
                user.setEmailVerificationTokenExpiration(LocalDateTime.now().plusDays(7));
                userRepository.save(user);
                emailService.sendVerificationEmail(user.getEmail(), verificationToken);
            }

            // Création de la réponse avec les tokens
            AuthResponse authResponse = createAuthResponse(user);
            return new ResponseEntity<>(
                    new ResponseDTO<>(authResponse, "Inscription réussie"),
                    HttpStatus.OK);

        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(
                    new ResponseDTO<>(null, "Erreur lors de l'enregistrement : données invalides"),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(
                    new ResponseDTO<>(null, "Une erreur technique est survenue, veuillez réessayer"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Authentifie un utilisateur avec son email et mot de passe.
     * Vérifie que l'utilisateur existe, est actif et que le mot de passe
     * correspond.
     * Ne fonctionne que pour les utilisateurs de type REGULAR.
     *
     * @param userLogin DTO contenant l'email et le mot de passe de l'utilisateur
     * @return ResponseEntity contenant :
     *         - 200 OK : Authentification réussie avec tokens et informations
     *         utilisateur
     *         - 400 BAD_REQUEST : Données de connexion manquantes ou invalides
     *         - 401 UNAUTHORIZED : Mot de passe incorrect
     *         - 404 NOT_FOUND : Utilisateur non trouvé ou inactif
     *         - 500 INTERNAL_SERVER_ERROR : Erreur technique lors du traitement
     */
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseDTO<Object>> loginUser(UserLoginDTO userLogin) {
        // Initialisation de la réponse
        ResponseDTO<Object> response = new ResponseDTO<>();

        try {
            // Validation des données d'entrée
            if (userLogin == null || userLogin.getEmail() == null || userLogin.getPassword() == null) {
                response.setMessage("Les informations de connexion sont incomplètes");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // Recherche de l'utilisateur actif par email
            Optional<User> userOptional = userRepository.findByEmailAndIsActiveTrue(userLogin.getEmail());


            // Vérifie si l'utilisateur existe et est de type REGULAR
            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // Vérifie le type d'utilisateur
                if (user.getType() != UserType.REGULAR) {
                    response.setMessage("Ce compte nécessite une authentification via un fournisseur externe");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }

                // Vérifie si le mot de passe est correct
                if (passwordService.verifyPassword(userLogin.getPassword(), user.getPassword())) {
                    // Création de la réponse d'authentification avec les tokens
                    AuthResponse authResponse = createAuthResponse(user);
                    response.setData(authResponse);
                    response.setMessage("Connexion réussie");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    response.setMessage("Mot de passe incorrect");
                    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
                }
            }

            // Cas où l'utilisateur n'existe pas ou n'est pas actif
            response.setMessage("Aucun compte actif n'a été trouvé avec cet email");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            // Log de l'erreur pour le debugging
            e.printStackTrace();
            response.setMessage("Une erreur technique est survenue lors de la connexion");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Enregistre un utilisateur via un fournisseur d'authentification externe.
     * Gère la création d'un nouveau compte ou la mise à jour d'un compte existant.
     *
     * @param registrationDTO DTO contenant les informations d'inscription (email,
     *                        provider, etc.)
     * @return ResponseEntity contenant :
     *         - 200 OK : Inscription réussie avec tokens d'authentification
     *         - 400 BAD_REQUEST : Provider inexistant ou données invalides
     *         - 409 CONFLICT : Compte existant sans provider
     *         - 500 INTERNAL_SERVER_ERROR : Erreur technique
     */
    @Transactional
    public ResponseEntity<ResponseDTO<Object>> registerUserProvider(UserRegistrationProviderDTO registrationDTO) {
        // Initialisation de la réponse
        ResponseDTO<Object> response = new ResponseDTO<>();

        try {
            // Validation des données d'entrée
            if (registrationDTO == null || registrationDTO.getProviderName() == null
                    || registrationDTO.getEmail() == null) {
                response.setMessage("Les données d'inscription sont invalides ou incomplètes");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // Recherche du provider dans la base de données
            Optional<Provider> providerOptional = providerRepository
                    .findByProviderName(registrationDTO.getProviderName());

            if (providerOptional.isEmpty()) {
                response.setMessage("Le fournisseur d'authentification spécifié n'existe pas");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            Provider provider = providerOptional.get();
            User user;

            // Vérification si l'utilisateur existe déjà
            Optional<User> userAlreadyExistOptional = userRepository.findByEmail(registrationDTO.getEmail());

            if (userAlreadyExistOptional.isPresent()) {
                User userAlreadyExist = userAlreadyExistOptional.get();
                boolean userHaveAlreadyAuthProvider = !userAlreadyExist.getAuthProviders().isEmpty();

                // Cas 1: Utilisateur existant et actif
                if (userAlreadyExist.getIsActive()) {
                    if (userHaveAlreadyAuthProvider) {
                        // L'utilisateur existe déjà avec un provider - on renvoie ses informations
                        AuthResponse authResponse = createAuthResponse(userAlreadyExist);
                        return new ResponseEntity<>(new ResponseDTO<>(authResponse, "Connexion effectuée avec succès"),
                                HttpStatus.OK);
                    } else {
                        // L'utilisateur existe mais sans provider - conflit
                        return new ResponseEntity<>(
                                new ResponseDTO<>("Un compte existe déjà avec cet email sans provider"),
                                HttpStatus.CONFLICT);
                    }
                }
                // Cas 2: Utilisateur existant mais inactif
                else {
                    if (userHaveAlreadyAuthProvider) {
                        // Réactivation du compte
                        updateUserFromRegistrationDTO(userAlreadyExist, registrationDTO);
                        userRepository.save(userAlreadyExist);
                        return new ResponseEntity<>(new ResponseDTO<>("Vous avez bien été inscrit avec succès"),
                                HttpStatus.ACCEPTED);
                    } else {
                        // Mise à jour et réactivation du compte
                        updateUserFromRegistrationDTO(userAlreadyExist, registrationDTO);
                        user = userRepository.save(userAlreadyExist);
                    }
                }
            } else {
                // Cas 3: Création d'un nouvel utilisateur
                user = convertToUserRegistrationDTO(registrationDTO);
                user = userRepository.save(user);
            }

            // Association du provider à l'utilisateur
            CreateAuthProviderDTO createAuthProviderDTO = new CreateAuthProviderDTO();
            createAuthProviderDTO.setAccountProviderId(registrationDTO.getAccountProviderId());
            createAuthProviderDTO.setProviderId(provider.getProviderId());
            createAuthProviderDTO.setUserId(user.getUserId());

            ResponseEntity<ResponseDTO<AuthProvider>> authProviderRegistration = authProviderService
                    .createAuthProvider(createAuthProviderDTO);

            if (authProviderRegistration.getStatusCode() != HttpStatus.CREATED) {
                return new ResponseEntity<>(new ResponseDTO<>("Échec de l'association avec le provider"),
                        HttpStatus.BAD_REQUEST);
            }

            // Création de la réponse avec les tokens d'authentification
            AuthResponse authResponse = createAuthResponse(user);
            return new ResponseEntity<>(new ResponseDTO<>(authResponse, "Inscription réussie via provider"),
                    HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ResponseDTO<>("Une erreur technique est survenue lors de l'inscription"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Convertit un DTO d'inscription via provider en entité User.
     * Initialise un nouvel utilisateur avec les informations fournies.
     *
     * @param registrationDTO DTO contenant les informations d'inscription
     * @return Nouvelle instance de User avec les informations du DTO
     * @throws IllegalArgumentException si l'email est manquant
     */
    private User convertToUserRegistrationDTO(UserRegistrationProviderDTO registrationDTO) {
        if (registrationDTO.getEmail() == null || registrationDTO.getEmail().isEmpty()) {
            throw new IllegalArgumentException("L'email est obligatoire");
        }

        User user = new User();
        user.setEmail(registrationDTO.getEmail());
        user.setFirstName(registrationDTO.getFirstName());
        user.setLastName(registrationDTO.getLastName());
        user.setBirthDate(registrationDTO.getBirthDate());
        user.setCity(registrationDTO.getCity());
        user.setPhoneNumber(registrationDTO.getPhoneNumber());
        user.setRole(registrationDTO.getRole() != null ? registrationDTO.getRole() : UserRole.USER);
        user.setUrlImageUser(registrationDTO.getUrlImageUser());
        user.setEmailVerified(registrationDTO.isEmailVerified());
        user.setType(UserType.PROVIDER);
        user.setIsActive(true);
        return user;
    }

    /**
     * Met à jour un utilisateur existant avec les informations d'un DTO.
     * Actualise tous les champs modifiables de l'utilisateur.
     *
     * @param user            Utilisateur à mettre à jour
     * @param registrationDTO DTO contenant les nouvelles informations
     * @return Utilisateur mis à jour et sauvegardé
     * @throws IllegalArgumentException si l'utilisateur ou le DTO est null
     */
    @Transactional
    private User updateUserFromRegistrationDTO(User user, UserRegistrationProviderDTO registrationDTO) {
        if (user == null || registrationDTO == null) {
            throw new IllegalArgumentException("L'utilisateur et le DTO ne peuvent pas être null");
        }

        user.setIsActive(true);
        user.setFirstName(registrationDTO.getFirstName());
        user.setLastName(registrationDTO.getLastName());
        user.setBirthDate(registrationDTO.getBirthDate());
        user.setCity(registrationDTO.getCity());
        user.setPhoneNumber(registrationDTO.getPhoneNumber());
        user.setRole(registrationDTO.getRole() != null ? registrationDTO.getRole() : UserRole.USER);
        user.setUrlImageUser(registrationDTO.getUrlImageUser());
        user.setEmailVerified(registrationDTO.isEmailVerified());
        return userRepository.save(user);
    }

    /**
     * Authentifie un utilisateur via un provider externe.
     * Vérifie l'existence de l'utilisateur et sa méthode d'authentification.
     *
     * @param loginDTO DTO contenant l'email pour la connexion
     * @return ResponseEntity contenant :
     *         - 200 OK : AuthResponse avec tokens si authentification réussie
     *         - 400 BAD_REQUEST : Utilisateur existant avec autre méthode d'auth
     *         - 404 NOT_FOUND : Utilisateur inexistant
     *         - 500 INTERNAL_SERVER_ERROR : Erreur technique
     */
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseDTO<Object>> loginUserProvider(UserLoginProviderDTO loginDTO) {
        ResponseDTO<Object> response = new ResponseDTO<>();

        // Validation des données d'entrée
        if (loginDTO == null || loginDTO.getEmail() == null || loginDTO.getEmail().isEmpty()) {
            response.setMessage("L'email est obligatoire");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            // Recherche de l'utilisateur avec authentification provider
            ResponseEntity<ResponseDTO<User>> responseSearchUser = authProviderService
                    .getUserWithAuthProvider(loginDTO.getEmail());

            // Recherche de l'utilisateur dans la base locale
            Optional<User> responseUserNotProvider = userRepository.findByEmail(loginDTO.getEmail());

            // Vérification si l'utilisateur n'a pas d'authentification provider
            if (responseSearchUser.getStatusCode() == HttpStatus.NOT_FOUND) {
                if (responseUserNotProvider.isPresent() && responseUserNotProvider.get().getIsActive()) {
                    response.setMessage("Cet email est déjà utilisé avec une autre méthode d'authentification");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                } else {
                    response.setMessage("Aucun compte n'existe avec cet email");
                    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
                }
            }

            // Récupération de l'utilisateur et création de la réponse
            ResponseDTO<User> userResponseDTO = responseSearchUser.getBody();
            User user;

            if (userResponseDTO == null) {
                response.setMessage("Aucun compte n'existe avec cet email");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            } else {
                user = userResponseDTO.getData();
            }

            AuthResponse authResponse = createAuthResponse(user);
            response.setData(authResponse);
            response.setMessage("Authentification réussie");

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Une erreur technique est survenue. Veuillez réessayer ultérieurement");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Crée une réponse d'authentification avec tokens et informations utilisateur.
     * Génère les tokens d'accès et de rafraîchissement.
     *
     * @param user Utilisateur pour lequel créer la réponse
     * @return AuthResponse contenant tokens et informations utilisateur
     * @throws IllegalArgumentException si l'utilisateur est null
     * @throws RuntimeException         si erreur lors de la création des tokens
     */
    private AuthResponse createAuthResponse(User user) {
        if (user == null) {
            throw new IllegalArgumentException("L'utilisateur ne peut pas être null");
        }

        try {
            // Génération des tokens d'accès et de rafraîchissement
            String accessToken = jwtService.generateAccessToken(user.getUserId());
            String refreshToken = jwtService.generateRefreshToken(user.getUserId());

            // Création de l'objet contenant les tokens
            TokenDTO tokens = new TokenDTO();
            tokens.setAccessToken(accessToken);
            tokens.setRefreshToken(refreshToken);

            // Création de la réponse d'authentification
            AuthResponse authResponse = new AuthResponse();
            authResponse.setTokens(tokens);
            authResponse.setUser(user);

            return authResponse;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création de la réponse d'authentification", e);
        }
    }

    /**
     * Rafraîchit le token d'accès en utilisant un refresh token valide.
     * Vérifie la validité du refresh token et génère un nouveau token d'accès.
     *
     * @param refreshToken Token de rafraîchissement à valider
     * @return ResponseEntity contenant :
     *         - 200 OK : Nouveau token d'accès
     *         - 400 BAD_REQUEST : Token invalide ou expiré
     *         - 401 UNAUTHORIZED : Token dans la liste noire
     *         - 500 INTERNAL_SERVER_ERROR : Erreur technique
     */
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseDTO<String>> refreshAccessToken(String refreshToken) {
        ResponseDTO<String> response = new ResponseDTO<>();
        try {
            // Vérifie si le token est valide et n'est pas dans la liste noire
            if (!jwtService.validateToken(refreshToken, "refresh")) {
                response.setMessage("Le token de rafraîchissement est invalide ou expiré");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            if (jwtService.isBlackList(refreshToken)) {
                response.setMessage("Le token de rafraîchissement est dans la liste noire");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

            // Génère un nouveau token d'accès
            String userId = jwtService.extractUserId(refreshToken);
            String newAccessToken = jwtService.generateAccessToken(Long.parseLong(userId));

            response.setData(newAccessToken);
            response.setMessage("Nouveau token d'accès généré avec succès");
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Une erreur est survenue lors du rafraîchissement du token");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Déconnecte un utilisateur en invalidant son refresh token.
     * Ajoute le refresh token à la liste noire pour empêcher sa réutilisation.
     *
     * @param refreshToken Token de rafraîchissement à invalider
     * @return ResponseEntity contenant :
     *         - 200 OK : Déconnexion réussie
     *         - 401 UNAUTHORIZED : Token déjà invalidé
     *         - 404 NOT_FOUND : Utilisateur non trouvé
     *         - 500 INTERNAL_SERVER_ERROR : Erreur technique
     */
    @Transactional
    public ResponseEntity<ResponseDTO<Void>> logoutUser(String refreshToken) {
        ResponseDTO<Void> response = new ResponseDTO<>();
        try {
            // Extraction de l'ID utilisateur du token
            String userId = jwtService.extractUserId(refreshToken);
            Optional<User> userOptional = userRepository.findById(Long.parseLong(userId));

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // Vérifie si le token n'est pas déjà dans la liste noire
                if (!user.getRefreshTokensBlacklist().stream()
                        .map(RefreshTokensBlacklist::getRefreshToken)
                        .collect(Collectors.toList())
                        .contains(refreshToken)) {

                    // Création d'une nouvelle entrée dans la liste noire
                    RefreshTokensBlacklist refreshTokenBlacklist = new RefreshTokensBlacklist();
                    refreshTokenBlacklist.setRefreshToken(refreshToken);
                    refreshTokenBlacklist.setUser(user);

                    refreshTokenBlacklistRepository.save(refreshTokenBlacklist);
                    response.setMessage("Déconnexion réussie");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }

                response.setMessage("Token déjà invalidé");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            } else {
                response.setMessage("Utilisateur non trouvé");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Une erreur est survenue, veuillez réessayer");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}