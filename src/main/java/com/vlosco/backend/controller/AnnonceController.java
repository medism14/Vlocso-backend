/**
 * Contrôleur REST pour la gestion des annonces.
 * Fournit les endpoints pour effectuer les opérations CRUD et les recherches sur les annonces.
 */
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vlosco.backend.dto.AnnonceCreationDTO;
import com.vlosco.backend.dto.AnnonceUpdateDTO;
import com.vlosco.backend.dto.AnnonceWithUserDTO;
import com.vlosco.backend.dto.PremiumAnnonceDTO;
import com.vlosco.backend.dto.ResponseDTO;
import com.vlosco.backend.model.Annonce;
import com.vlosco.backend.service.AnnonceService;
import com.vlosco.backend.service.ImageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Annonces", description = "API pour la gestion des annonces")
@RestController
@RequestMapping("/annonces")
@CrossOrigin(origins = "*")
public class AnnonceController {

        private final AnnonceService annonceService;
        private final ImageService imageServices;

        @Autowired
        public AnnonceController(AnnonceService annonceService, ImageService imageServices) {
                this.annonceService = annonceService;
                this.imageServices = imageServices;
        }

        @Operation(summary = "Récupérer toutes les annonces", description = "Retourne la liste complète des annonces actives")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Liste des annonces récupérée avec succès"),
                        @ApiResponse(responseCode = "204", description = "Aucune annonce trouvée"),
                        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
        })
        @GetMapping
        public ResponseEntity<ResponseDTO<List<AnnonceWithUserDTO>>> getAllAnnonces() {
                return annonceService.getAllAnnonces();
        }

        @Operation(summary = "Récupérer une annonce par ID", description = "Retourne une annonce spécifique basée sur son ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Annonce trouvée"),
                        @ApiResponse(responseCode = "404", description = "Annonce non trouvée"),
                        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
        })
        @GetMapping("/{id}")
        public ResponseEntity<ResponseDTO<AnnonceWithUserDTO>> getAnnonceById(
                        @Parameter(description = "ID de l'annonce à récupérer") @PathVariable Long id) {
                return annonceService.getAnnonceById(id);
        }

        @Operation(summary = "Récupérer les annonces d'un utilisateur", description = "Retourne toutes les annonces associées à un utilisateur spécifique")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Annonces de l'utilisateur trouvées"),
                        @ApiResponse(responseCode = "204", description = "Aucune annonce trouvée pour cet utilisateur"),
                        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
        })
        @GetMapping("/user/{userId}")
        public ResponseEntity<ResponseDTO<List<AnnonceWithUserDTO>>> getAnnoncesByUserId(
                        @Parameter(description = "ID de l'utilisateur") @PathVariable Long userId) {
                return annonceService.getAnnoncesByUserId(userId);
        }

        @Operation(summary = "Récupérer les recommandations pour un utilisateur", 
                  description = "Retourne une liste d'annonces recommandées pour l'utilisateur spécifié")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recommandations trouvées avec succès"),
            @ApiResponse(responseCode = "404", description = "Aucune recommandation trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
        })
        @PostMapping("/recommandation/{userId}")
        public ResponseEntity<ResponseDTO<List<Annonce>>> getRecommandationsUser(
            @Parameter(description = "ID de l'utilisateur") @PathVariable Long userId,
            @Parameter(description = "Type de véhicule (valeurs possibles: 'general', 'voitures', 'motos')") @RequestParam String type,
            @Parameter(description = "Nombre d'annonces à recommander (12 en général)") @RequestParam Integer nbAnnonces,
            @Parameter(description = "Liste des IDs d'annonces à exclure") 
            @RequestBody(required = false) List<Long> excludeIds) {
            return annonceService.recommandationUser(userId, type, nbAnnonces, excludeIds);
        }

        @Operation(summary = "Créer une nouvelle annonce", description = "Crée une nouvelle annonce avec les détails fournis")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Annonce créée avec succès"),
                        @ApiResponse(responseCode = "400", description = "Données invalides"),
                        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
                        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
        })
        @PostMapping
        public ResponseEntity<ResponseDTO<AnnonceWithUserDTO>> createAnnonce(
                        @Parameter(description = "Détails de l'annonce à créer") @RequestBody AnnonceCreationDTO annonce) {
                return annonceService.createAnnonce(annonce);
        }

        @Operation(summary = "Mettre à jour une annonce", description = "Met à jour une annonce existante avec les nouvelles informations")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Annonce mise à jour avec succès"),
                        @ApiResponse(responseCode = "400", description = "Données invalides"),
                        @ApiResponse(responseCode = "404", description = "Annonce non trouvée"),
                        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
        })
        @PutMapping("/{id}")
        public ResponseEntity<ResponseDTO<AnnonceWithUserDTO>> updateAnnonce(
                        @Parameter(description = "ID de l'annonce à modifier") @PathVariable Long id,
                        @Parameter(description = "Nouvelles informations de l'annonce") @RequestBody AnnonceUpdateDTO annonce) {
                return annonceService.updateAnnonce(id, annonce);
        }

        @Operation(summary = "Supprimer une annonce", description = "Supprime une annonce spécifique du système")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Annonce supprimée avec succès"),
                        @ApiResponse(responseCode = "404", description = "Annonce non trouvée"),
                        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<ResponseDTO<Void>> deleteAnnonce(
                        @Parameter(description = "ID de l'annonce à supprimer") @PathVariable Long id) {
                return annonceService.deleteAnnonce(id);
        }

        @Operation(summary = "Rechercher des annonces", description = "Recherche des annonces par mot-clé dans le titre")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Annonces trouvées"),
                        @ApiResponse(responseCode = "204", description = "Aucune annonce trouvée"),
                        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
        })
        @GetMapping("/search")
        public ResponseEntity<ResponseDTO<List<Annonce>>> searchAnnonces(
                        @Parameter(description = "Mot-clé de recherche") @RequestParam String keyword) {
                return annonceService.searchAnnonces(keyword);
        }

        @Operation(summary = "Filtrer par catégorie", description = "Filtre les annonces par type de transaction")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Annonces filtrées avec succès"),
                        @ApiResponse(responseCode = "204", description = "Aucune annonce trouvée"),
                        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
        })
        @GetMapping("/category/{category}")
        public ResponseEntity<ResponseDTO<List<Annonce>>> getAnnoncesByTransaction(
                        @Parameter(description = "Catégorie de transaction") @PathVariable String category) {
                return annonceService.getAnnoncesByTransaction(category);
        }

        @Operation(summary = "Filtrer par localisation", description = "Récupère les annonces par pays et ville")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Annonces trouvées pour la localisation"),
                        @ApiResponse(responseCode = "204", description = "Aucune annonce trouvée"),
                        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
        })
        @GetMapping("/location")
        public ResponseEntity<ResponseDTO<List<Annonce>>> getAnnoncesByLocation(
                        @Parameter(description = "Pays des annonces") @RequestParam String country,
                        @Parameter(description = "Ville des annonces") @RequestParam String city) {
                return annonceService.getAnnoncesByLocation(country, city);
        }

        @Operation(summary = "Récupérer une annonce premium", description = "Récupère les détails d'une annonce premium par son identifiant")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Annonce premium récupérée avec succès"),
                        @ApiResponse(responseCode = "404", description = "Annonce premium non trouvée"),
                        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
        })
        @GetMapping("/{id}/premium")
        public ResponseEntity<ResponseDTO<PremiumAnnonceDTO>> getPremiumAnnonce(
                        @Parameter(description = "Identifiant de l'annonce premium") @PathVariable Long id) {
                return annonceService.getPremiumAnnonce(id);
        }

        @Operation(summary = "Filtrer les annonces", description = "Filtre les annonces selon différents critères spécifiés")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Annonces filtrées avec succès"),
                        @ApiResponse(responseCode = "204", description = "Aucune annonce ne correspond aux critères"),
                        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
        })
        @GetMapping("/filter")
        public ResponseEntity<ResponseDTO<List<Annonce>>> filterAnnonces(
                        @Parameter(description = "ID de l'utilisateur") @RequestParam(required = false) Long vendorId,
                        @Parameter(description = "État de l'annonce") @RequestParam(required = false) String annonce_state,
                        @Parameter(description = "Titre de l'annonce") @RequestParam(required = false) String title,
                        @Parameter(description = "Statut premium") @RequestParam(required = false) Boolean premium,
                        @Parameter(description = "Types de véhicules") @RequestParam(required = false) String vehicles,
                        @Parameter(description = "Critère de tri") @RequestParam(required = false) String sort,
                        @Parameter(description = "Type de transaction") @RequestParam(required = false) String transaction,
                        @Parameter(description = "Critères additionnels") @RequestParam(required = false) String annonce,
                        @Parameter(description = "Kilométrage minimum") @RequestParam(required = false) Integer minKilometrage,
                        @Parameter(description = "Kilométrage maximum") @RequestParam(required = false) Integer maxKilometrage,
                        @Parameter(description = "Prix minimum") @RequestParam(required = false) Double minPrice,
                        @Parameter(description = "Prix maximum") @RequestParam(required = false) Double maxPrice,
                        @Parameter(description = "Ville") @RequestParam(required = false) String city,
                        @Parameter(description = "Marque du véhicule") @RequestParam(required = false) String marque) {
                return annonceService.filterAnnonces(vendorId, annonce_state, title, premium,
                                vehicles, sort, transaction, annonce, minKilometrage, maxKilometrage,
                                minPrice, maxPrice, city, marque);
        }

        @Operation(summary = "Récupérer les images d'une annonce", description = "Récupère toutes les images associées à une annonce spécifique")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Images récupérées avec succès"),
                        @ApiResponse(responseCode = "204", description = "Aucune image trouvée"),
                        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
        })
        @GetMapping("/{id}/images")
        public ResponseEntity<ResponseDTO<List<String>>> getImagesByAnnonceId(
                        @Parameter(description = "ID de l'annonce") @PathVariable Long id) {
                return imageServices.getImagesByAnnonceId(id);
        }

        @Operation(summary = "Trouver des annonces similaires", 
                  description = "Trouve des annonces similaires basées sur une annonce spécifique")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Annonces similaires trouvées"),
            @ApiResponse(responseCode = "404", description = "Annonce de référence non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
        })
        @GetMapping("/similar/{annonceId}")
        public ResponseEntity<ResponseDTO<List<Annonce>>> getSimilarAnnonces(
            @Parameter(description = "ID de l'annonce de référence") 
            @PathVariable Long annonceId,
            @Parameter(description = "Nombre d'annonces similaires à retourner") 
            @RequestParam(defaultValue = "4") Integer nbAnnonces) {
            return annonceService.findSimilarAnnonces(annonceId, nbAnnonces);
        }

}
