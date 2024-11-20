package com.vlosco.backend.controller;

import com.vlosco.backend.dto.ResponseDTO;
import com.vlosco.backend.dto.UserUpdateDTO;
import com.vlosco.backend.dto.UserUpdatePasswordDto;
import com.vlosco.backend.model.User;
import com.vlosco.backend.service.JwtService;
import com.vlosco.backend.service.UserService;
import org.springframework.web.bind.annotation.CrossOrigin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

/**
 * Contrôleur REST gérant toutes les opérations liées aux utilisateurs.
 * Fournit des endpoints pour la gestion des comptes, l'authentification et les mises à jour.
 */
@Tag(name = "User Management", description = "Endpoints pour la gestion des utilisateurs")
@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @Operation(summary = "Récupérer tous les utilisateurs actifs", 
              description = "Retourne la liste des utilisateurs dont le compte est actif")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des utilisateurs récupérée avec succès"),
        @ApiResponse(responseCode = "204", description = "Aucun utilisateur actif trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping
    public ResponseEntity<ResponseDTO<List<User>>> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Récupérer un utilisateur par ID",
              description = "Retourne les détails d'un utilisateur spécifique basé sur son ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Utilisateur trouvé et retourné avec succès"),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<User>> getUserById(
        @Parameter(description = "ID de l'utilisateur à récupérer", required = true)
        @PathVariable Long id
    ) {
        return userService.getUserById(id);
    }

    @Operation(summary = "Extraire les informations utilisateur du token JWT",
              description = "Décode et retourne les informations utilisateur contenues dans le token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Informations utilisateur extraites avec succès"),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur lors du décodage du token")
    })
    @GetMapping("/by-token/{token}")
    public ResponseEntity<ResponseDTO<User>> getUserInfoByToken(
        @Parameter(description = "Token JWT à décoder", required = true)
        @PathVariable String token
    ) {
        return jwtService.extractUserInfo(token);
    }

    @Operation(summary = "Récupérer un utilisateur par email",
              description = "Recherche et retourne un utilisateur actif par son adresse email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Utilisateur trouvé et retourné avec succès"),
        @ApiResponse(responseCode = "404", description = "Aucun utilisateur actif trouvé avec cet email"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping("/by-email/{email}")
    public ResponseEntity<ResponseDTO<User>> getUserByEmail(
        @Parameter(description = "Email de l'utilisateur à rechercher", required = true)
        @PathVariable String email
    ) {
        return userService.getUserByEmail(email);
    }

    @Operation(summary = "Initier la confirmation d'email",
              description = "Génère un token de vérification et envoie un email de confirmation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email de confirmation envoyé avec succès"),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur lors de l'envoi de l'email")
    })
    @PostMapping("/email-confirmation-send/{email}")
    public ResponseEntity<ResponseDTO<Void>> launchEmailConfirmation(
        @Parameter(description = "Email à confirmer", required = true)
        @PathVariable String email
    ) {
        return userService.launchVerifEmail(email);
    }

    @Operation(summary = "Vérifier le token de confirmation d'email",
              description = "Valide le token de confirmation et active l'email de l'utilisateur")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email vérifié avec succès"),
        @ApiResponse(responseCode = "400", description = "Token invalide ou expiré"),
        @ApiResponse(responseCode = "404", description = "Token non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur lors de la vérification")
    })
    @GetMapping("/verif-email-confirmation")
    public ResponseEntity<String> verifEmailConfirmation(
        @Parameter(description = "Token de vérification d'email", required = true)
        @RequestParam String token
    ) {
        ResponseEntity<String> responseEntity = userService.verifyEmail(token);

        String resultat = responseEntity.getBody();
        HttpStatusCode statut = responseEntity.getStatusCode();

        String corpsDeReponse = """
                    <!DOCTYPE html>
                        <html lang="fr">
                            <head>
                                <meta charset="UTF-8">
                                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
                            </head>
                            <body style="min-height: 100vh; width: 100vw; display: flex; justify-content: center; align-items: center; margin: 0;">
                                <div style="display: flex; flex-direction: column; align-items: center; gap: 5px; margin: 0; padding: 0">
                                    <h1 style="color: #333333; margin: 0">%s</h1>
                                    <h4 style="color: #888888; margin: 0">Vous pouvez fermer cet onglet</h4>
                                </div>
                            </body>
                        </html>
                """
                .formatted(resultat != null ? resultat : "Erreur inconnue");

        return ResponseEntity.status(statut)
                .contentType(MediaType.TEXT_HTML)
                .body(corpsDeReponse);
    }

    @Operation(summary = "Initier la réinitialisation de mot de passe",
              description = "Génère un token de réinitialisation et envoie un email avec les instructions")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email de réinitialisation envoyé avec succès"),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur lors de l'envoi de l'email")
    })
    @PostMapping("/update-password-send/{email}")
    public ResponseEntity<ResponseDTO<Void>> launchUpdatePassword(
        @Parameter(description = "Email de l'utilisateur", required = true)
        @PathVariable String email
    ) {
        return userService.launchUpdatePassword(email);
    }

    @Operation(summary = "Vérifier le token de réinitialisation",
              description = "Vérifie la validité du token de réinitialisation de mot de passe")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token valide, utilisateur retourné"),
        @ApiResponse(responseCode = "400", description = "Token expiré"),
        @ApiResponse(responseCode = "404", description = "Token non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur lors de la vérification")
    })
    @GetMapping("/verif-update-password")
    public ResponseEntity<ResponseDTO<User>> verifUpdatePassword(
        @Parameter(description = "Token de réinitialisation", required = true)
        @RequestParam String token
    ) {
        return userService.verifUpdatePassword(token);
    }

    @Operation(summary = "Mettre à jour le mot de passe",
              description = "Met à jour le mot de passe de l'utilisateur après réinitialisation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mot de passe mis à jour avec succès"),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur lors de la mise à jour")
    })
    @PostMapping("/update-password")
    public ResponseEntity<ResponseDTO<Void>> updatePassword(
        @Parameter(description = "Informations de mise à jour du mot de passe", required = true)
        @RequestBody UserUpdatePasswordDto userUpdatePassword
    ) {
        return userService.updatePassword(userUpdatePassword);
    }

    @Operation(summary = "Mettre à jour un utilisateur",
              description = "Met à jour les informations d'un utilisateur existant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Utilisateur mis à jour avec succès"),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur lors de la mise à jour")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<User>> updateUser(
        @Parameter(description = "ID de l'utilisateur à mettre à jour", required = true)
        @PathVariable Long id,
        @Parameter(description = "Nouvelles informations utilisateur", required = true)
        @RequestBody UserUpdateDTO userDetails
    ) {
        return userService.updateUser(id, userDetails);
    }

    @Operation(summary = "Désactiver un utilisateur",
              description = "Désactive le compte d'un utilisateur (soft delete)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Utilisateur désactivé avec succès"),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur lors de la désactivation")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<Void>> removeUser(
        @Parameter(description = "ID de l'utilisateur à désactiver", required = true)
        @PathVariable Long id
    ) {
        return userService.removeUser(id);
    }
}