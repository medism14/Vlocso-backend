package com.vlosco.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.vlosco.backend.dto.ResponseDTO;
import com.vlosco.backend.model.Image;
import com.vlosco.backend.service.ImageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Contrôleur REST pour la gestion des images
 * Fournit les endpoints pour les opérations CRUD sur les images
 */
@Tag(name = "Images", description = "API pour la gestion des images")
@RestController
@RequestMapping("/images")
@CrossOrigin(origins = "*")
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @Operation(summary = "Sauvegarder une nouvelle image",
              description = "Enregistre une nouvelle image à partir d'une URL pour une annonce spécifique")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Image sauvegardée avec succès"),
        @ApiResponse(responseCode = "400", description = "URL invalide ou vide"),
        @ApiResponse(responseCode = "404", description = "Annonce non trouvée"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @PostMapping("/{annonceId}")
    public ResponseEntity<ResponseDTO<Image>> saveImage(
            @Parameter(description = "URL de l'image à sauvegarder") @RequestBody String imageUrl,
            @Parameter(description = "ID de l'annonce associée") @PathVariable Long annonceId) {
        return imageService.saveImage(imageUrl, annonceId);
    }

    @Operation(summary = "Récupérer une image par ID",
              description = "Retourne une image spécifique basée sur son ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Image trouvée"),
        @ApiResponse(responseCode = "404", description = "Image non trouvée"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<Image>> getImage(
            @Parameter(description = "ID de l'image à récupérer") @PathVariable Long id) {
        return imageService.getImage(id);
    }

    @Operation(summary = "Supprimer une image",
              description = "Supprime une image spécifique par son ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Image supprimée avec succès"),
        @ApiResponse(responseCode = "404", description = "Image non trouvée"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<Void>> deleteImage(
            @Parameter(description = "ID de l'image à supprimer") @PathVariable Long id) {
        return imageService.deleteImage(id);
    }

    @Operation(summary = "Récupérer toutes les images",
              description = "Retourne la liste de toutes les images stockées")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des images récupérée avec succès"),
        @ApiResponse(responseCode = "204", description = "Aucune image trouvée"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping
    public ResponseEntity<ResponseDTO<List<Image>>> getAllImages() {
        return imageService.getAllImages();
    }

    /**
     * Récupère toutes les images associées à une annonce spécifique
     * @param annonceId L'identifiant de l'annonce
     * @return ResponseEntity contenant la liste des images de l'annonce
     */
    @Operation(summary = "Récupérer les URLs des images d'une annonce",
              description = "Retourne toutes les URLs des images associées à une annonce spécifique")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "URLs des images de l'annonce récupérées avec succès"),
        @ApiResponse(responseCode = "204", description = "Aucune image trouvée pour cette annonce"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping("/by-annonce/{annonceId}")
    public ResponseEntity<ResponseDTO<List<String>>> getImagesByAnnonceId(@PathVariable Long annonceId) {
        return imageService.getImagesByAnnonceId(annonceId);
    }
}