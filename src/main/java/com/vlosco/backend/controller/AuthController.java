/**
 * Contrôleur REST gérant l'authentification des utilisateurs.
 * Fournit les endpoints pour l'inscription, la connexion et la gestion des tokens.
 */
package com.vlosco.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import com.vlosco.backend.dto.RefreshTokenDTO;
import com.vlosco.backend.dto.ResponseDTO;
import com.vlosco.backend.dto.UserLoginDTO;
import com.vlosco.backend.dto.UserLoginProviderDTO;
import com.vlosco.backend.dto.UserRegistrationDTO;
import com.vlosco.backend.dto.UserRegistrationProviderDTO;
import com.vlosco.backend.service.AuthService;

@Tag(name = "Authentication", description = "Endpoints pour l'authentification des utilisateurs")
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    /**
     * Constructeur avec injection de dépendance du service d'authentification
     */
    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    @Operation(summary = "Inscription d'un nouvel utilisateur", 
              description = "Enregistre un nouvel utilisateur avec ses informations de base")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inscription réussie"),
        @ApiResponse(responseCode = "400", description = "Données d'inscription invalides"),
        @ApiResponse(responseCode = "409", description = "Email déjà utilisé"),
        @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<Object>> registerUser(
        @Parameter(description = "Informations d'inscription de l'utilisateur", required = true)
        @RequestBody UserRegistrationDTO registrationDTO) {
        return authService.registerUser(registrationDTO);
    }

    @Operation(summary = "Inscription via provider externe", 
              description = "Enregistre un utilisateur via un fournisseur d'authentification (Google, Facebook, etc.)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inscription réussie"),
        @ApiResponse(responseCode = "400", description = "Provider invalide ou données incorrectes"),
        @ApiResponse(responseCode = "409", description = "Compte existant sans provider"),
        @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PostMapping("/register/provider")
    public ResponseEntity<ResponseDTO<Object>> registerUserProvider(
        @Parameter(description = "Informations d'inscription via provider", required = true)
        @RequestBody UserRegistrationProviderDTO registrationDTO) {
        return authService.registerUserProvider(registrationDTO);
    }

    @Operation(summary = "Connexion utilisateur", 
              description = "Authentifie un utilisateur avec email et mot de passe")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Connexion réussie",
                    content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ResponseDTO.class,
                            description = "Réponse d'authentification"
                        )
                    )),
        @ApiResponse(responseCode = "400", description = "Données de connexion invalides"),
        @ApiResponse(responseCode = "401", description = "Authentification échouée"),
        @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<Object>> loginUser(
        @Parameter(description = "Identifiants de connexion", required = true)
        @RequestBody UserLoginDTO loginDTO) {
        return authService.loginUser(loginDTO);
    }

    @Operation(summary = "Connexion via provider", 
              description = "Authentifie un utilisateur via un provider externe")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Connexion réussie"),
        @ApiResponse(responseCode = "400", description = "Méthode d'authentification incorrecte"),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PostMapping("/login/provider")
    public ResponseEntity<ResponseDTO<Object>> loginUserProvider(
        @Parameter(description = "Informations de connexion provider", required = true)
        @RequestBody UserLoginProviderDTO loginDTO) {
        return authService.loginUserProvider(loginDTO);
    }

    @Operation(summary = "Rafraîchissement du token", 
              description = "Génère un nouveau token d'accès à partir d'un refresh token valide")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Nouveau token généré"),
        @ApiResponse(responseCode = "400", description = "Token invalide"),
        @ApiResponse(responseCode = "401", description = "Token blacklisté"),
        @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PostMapping("/refresh/{refreshToken}")
    public ResponseEntity<ResponseDTO<String>> refreshToken(
        @Parameter(description = "Refresh token à utiliser", required = true)
        @PathVariable String refreshToken) {
        return authService.refreshAccessToken(refreshToken);
    }

    @Operation(summary = "Déconnexion", 
              description = "Invalide le refresh token de l'utilisateur")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Déconnexion réussie"),
        @ApiResponse(responseCode = "401", description = "Token déjà invalidé"),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PostMapping("/logout")
    public ResponseEntity<ResponseDTO<Void>> logoutUser(
        @Parameter(description = "Refresh token à invalider", required = true)
        @RequestBody RefreshTokenDTO refreshToken) {
        return authService.logoutUser(refreshToken.getRefreshToken());
    }
}