package com.vlosco.backend.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.vlosco.backend.dto.ResponseDTO;
import com.vlosco.backend.model.Annonce;
import com.vlosco.backend.model.Image;
import com.vlosco.backend.repository.AnnonceRepository;
import com.vlosco.backend.repository.ImageRepository;

/**
 * Service responsable de la gestion des images dans l'application.
 * Fournit des opérations CRUD pour manipuler les URLs d'images stockées en base de données.
 * Gère également la récupération des images par annonce.
 */
@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final AnnonceRepository annonceRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository, AnnonceRepository annonceRepository) {
        this.imageRepository = imageRepository;
        this.annonceRepository = annonceRepository;
    }
    
    /**
     * Sauvegarde une nouvelle image en base de données.
     * Effectue une validation de l'URL avant la sauvegarde.
     * 
     * @param imageUrl L'URL de l'image à sauvegarder, ne doit pas être null ou vide
     * @return ResponseEntity contenant:
     *         - 200 OK: Image sauvegardée avec succès
     *         - 400 BAD_REQUEST: Si l'URL est null ou vide
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    public ResponseEntity<ResponseDTO<Image>> saveImage(String imageUrl, Long annonceId) {
        ResponseDTO<Image> response = new ResponseDTO<>();
        
        try {
            // Validation de base de l'URL
            if (imageUrl == null || imageUrl.isEmpty()) {
                response.setMessage("L'URL de l'image est vide ou invalide");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            
            // Récupération de l'annonce par son identifiant
            Optional<Annonce> annonceOptional = annonceRepository.findById(annonceId);
            if (!annonceOptional.isPresent()) {
                response.setMessage("Aucune annonce trouvée avec l'id: " + annonceId);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            
            // Création et persistance de l'entité Image
            Image image = new Image();
            image.setImageUrl(imageUrl);
            image.setAnnonce(annonceOptional.get());
            
            Image savedImage = imageRepository.save(image);
            response.setMessage("L'URL de l'image a été sauvegardée avec succès");
            response.setData(savedImage);
            
            return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Une erreur est survenue lors de la sauvegarde de l'URL de l'image");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère une image spécifique par son identifiant.
     * 
     * @param id L'identifiant unique de l'image à récupérer
     * @return ResponseEntity contenant:
     *         - 200 OK: Image trouvée avec succès
     *         - 404 NOT_FOUND: Si aucune image ne correspond à l'ID
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    public ResponseEntity<ResponseDTO<Image>> getImage(Long id) {
        ResponseDTO<Image> response = new ResponseDTO<>();
        
        try {
            Optional<Image> image = imageRepository.findById(id);
            
            if (!image.isPresent()) {
                response.setMessage("Aucune image trouvée avec l'id: " + id);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            
            response.setMessage("L'image a été récupérée avec succès");
            response.setData(image.get());
            return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Une erreur est survenue lors de la récupération de l'image");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Supprime une image de la base de données.
     * Vérifie l'existence de l'image avant la suppression.
     * 
     * @param id L'identifiant unique de l'image à supprimer
     * @return ResponseEntity contenant:
     *         - 200 OK: Image supprimée avec succès
     *         - 404 NOT_FOUND: Si aucune image ne correspond à l'ID
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    public ResponseEntity<ResponseDTO<Void>> deleteImage(Long id) {
        ResponseDTO<Void> response = new ResponseDTO<>();
        
        try {
            // Vérification que l'ID n'est pas null
            if (id == null) {
                response.setMessage("L'identifiant de l'image ne peut pas être null");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            Optional<Image> image = imageRepository.findById(id);
            
            if (image.isEmpty()) {
                response.setMessage("Aucune image trouvée avec l'id: " + id);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            
            // Suppression de l'image
            try {
                imageRepository.delete(image.get());
            } catch (Exception e) {
                response.setMessage("Erreur lors de la suppression de l'image en base de données");
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            response.setMessage("L'image a été supprimée avec succès");
            return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Une erreur technique est survenue lors de la suppression de l'image");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Supprime toutes les images associées à une annonce spécifique.
     * 
     * @param annonceId L'identifiant unique de l'annonce dont on veut supprimer les images
     * @return ResponseEntity contenant:
     *         - 200 OK: Images supprimées avec succès
     *         - 404 NOT_FOUND: Si aucune image n'est associée à l'annonce
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    public ResponseEntity<ResponseDTO<Void>> deleteImagesByAnnonceId(Long annonceId) {
        ResponseDTO<Void> response = new ResponseDTO<>();
        
        try {
            // Vérification que l'ID de l'annonce n'est pas null
            if (annonceId == null) {
                response.setMessage("L'identifiant de l'annonce ne peut pas être null");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            List<Image> images = imageRepository.findByAnnonceId(annonceId);
            
            if (images.isEmpty()) {
                response.setMessage("Aucune image trouvée pour l'annonce avec l'id: " + annonceId);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            
            // Suppression des images
            try {
                imageRepository.deleteAll(images);
            } catch (Exception e) {
                response.setMessage("Erreur lors de la suppression des images en base de données");
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            response.setMessage("Les images ont été supprimées avec succès");
            return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Une erreur technique est survenue lors de la suppression des images");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère toutes les images stockées dans la base de données.
     * 
     * @return ResponseEntity contenant:
     *         - 200 OK: Liste des images trouvées
     *         - 204 NO_CONTENT: Si aucune image n'existe en base
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    public ResponseEntity<ResponseDTO<List<Image>>> getAllImages() {
        ResponseDTO<List<Image>> response = new ResponseDTO<>();
        
        try {
            List<Image> images = imageRepository.findAll();
            
            if (images.isEmpty()) {
                response.setMessage("Aucune image trouvée");
                return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
            }
            
            response.setMessage("Les images ont été récupérées avec succès");
            response.setData(images);
            return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Une erreur est survenue lors de la récupération des images");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère toutes les images associées à une annonce spécifique.
     * 
     * @param annonceId L'identifiant unique de l'annonce dont on veut récupérer les images
     * @return ResponseEntity contenant:
     *         - 200 OK: Liste des images associées à l'annonce
     *         - 204 NO_CONTENT: Si aucune image n'est associée à l'annonce
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    public ResponseEntity<ResponseDTO<List<Image>>> getImagesByAnnonce(Long annonceId) {
        ResponseDTO<List<Image>> response = new ResponseDTO<>();
        
        try {
            // Récupération des images liées à l'annonce spécifiée
            List<Image> images = imageRepository.findByAnnonceId(annonceId);
            
            if (images.isEmpty()) {
                response.setMessage("Aucune image trouvée pour cette annonce");
                return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
            }
            
            response.setMessage("Les images de l'annonce ont été récupérées avec succès");
            response.setData(images);
            return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Une erreur est survenue lors de la récupération des images de l'annonce");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère les images associées à une annonce spécifique par son identifiant.
     * 
     * @param annonceId Identifiant unique de l'annonce pour laquelle récupérer les images.
     * @return ResponseEntity<ResponseDTO<List<String>>> contenant les URLs des images associées à l'annonce.
     */
    public ResponseEntity<ResponseDTO<List<String>>> getImagesByAnnonceId(Long annonceId) {
        ResponseDTO<List<String>> response = new ResponseDTO<>();
        
        try {
            // Récupération des images liées à l'annonce spécifiée
            List<Image> images = imageRepository.findByAnnonceId(annonceId);
            
            if (images.isEmpty()) {
                response.setMessage("Aucune image trouvée pour l'annonce avec l'ID: " + annonceId);
                return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
            }

            List<String> imageUrls = images.stream().map(Image::getImageUrl).collect(Collectors.toList());
            response.setMessage("Images récupérées avec succès");
            response.setData(imageUrls);
            return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Une erreur est survenue lors de la récupération des images de l'annonce");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}