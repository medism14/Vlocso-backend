package com.vlosco.backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.vlosco.backend.dto.AnnonceCreationDTO;
import com.vlosco.backend.dto.AnnonceDetailsUpdateDTO;
import com.vlosco.backend.dto.AnnonceUpdateDTO;
import com.vlosco.backend.dto.AnnonceWithUserDTO;
import com.vlosco.backend.dto.PremiumAnnonceDTO;
import com.vlosco.backend.dto.ResponseDTO;
import com.vlosco.backend.enums.AnnonceState;
import com.vlosco.backend.model.Annonce;
import com.vlosco.backend.model.User;
import com.vlosco.backend.model.Vehicle;
import com.vlosco.backend.repository.AnnonceRepository;
import com.vlosco.backend.repository.InteractionRepository;
import com.vlosco.backend.repository.UserRepository;

/**
 * Service gérant les annonces de véhicules.
 * Fournit les opérations CRUD et des fonctionnalités de recherche avancée pour
 * les annonces.
 * Gère la validation des données, les relations avec les véhicules et le
 * traitement des erreurs.
 */
@Service
public class AnnonceService {

    private final AnnonceRepository annonceRepository;
    private final RestTemplate restTemplate;

    private final VehicleService vehicleService;
    private final InteractionRepository interactionRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;

    public AnnonceService(AnnonceRepository annonceRepository, VehicleService vehicleService,
            UserRepository userRepository, ImageService imageService, RestTemplate restTemplate,
            InteractionRepository interactionRepository) {
        this.annonceRepository = annonceRepository;
        this.vehicleService = vehicleService;
        this.userRepository = userRepository;
        this.imageService = imageService;
        this.restTemplate = restTemplate;
        this.interactionRepository = interactionRepository;
    }

    /**
     * Récupère toutes les annonces actives du système, incluant les détails des
     * utilisateurs correspondants.
     * 
     * @return ResponseEntity contenant:
     *         - 200 OK: Liste des annonces trouvées avec message de succès et
     *         détails des utilisateurs
     *         - 204 NO_CONTENT: Si aucune annonce n'existe
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    public ResponseEntity<ResponseDTO<List<AnnonceWithUserDTO>>> getAllAnnonces() {
        ResponseDTO<List<AnnonceWithUserDTO>> response = new ResponseDTO<>();
        try {
            List<Annonce> annonces = annonceRepository.findAll();
            if (annonces.isEmpty()) {
                response.setMessage("Aucune annonce trouvée");
                return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
            }
            List<AnnonceWithUserDTO> annonceDTOs = annonces.stream()
                    .map(annonce -> new AnnonceWithUserDTO(annonce, annonce.getVendor())) // Assurez-vous que
                                                                                          // AnnonceWithUserDTO prend
                                                                                          // Annonce et User
                    .collect(Collectors.toList());
            response.setMessage("Les annonces ont été récupérées avec succès");
            response.setData(annonceDTOs);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Une erreur est survenue lors de la récupération des annonces");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère une annonce spécifique par son identifiant unique.
     * 
     * @param id Identifiant unique de l'annonce recherchée
     * @return ResponseEntity contenant:
     *         - 200 OK: L'annonce trouvée avec message de succès et détails de
     *         l'utilisateur
     *         - 404 NOT_FOUND: Si aucune annonce ne correspond à l'ID
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    public ResponseEntity<ResponseDTO<AnnonceWithUserDTO>> getAnnonceById(Long id) {
        ResponseDTO<AnnonceWithUserDTO> response = new ResponseDTO<>();
        try {
            Optional<Annonce> annonce = annonceRepository.findById(id);
            if (!annonce.isPresent()) {
                response.setMessage("Aucune annonce trouvée avec l'ID: " + id);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            AnnonceWithUserDTO annonceWithUserDTO = new AnnonceWithUserDTO(annonce.get(), annonce.get().getVendor());
            response.setMessage("L'annonce a été récupérée avec succès");
            response.setData(annonceWithUserDTO);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Une erreur est survenue lors de la récupération de l'annonce");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer les recommandations qui pourrait être faite pour un utilisateur
     * 
     * @param userId ID de l'utilisateur
     * @param type   Type de véhicule ("Voiture", "Moto" ou null pour général)
     * @return ResponseEntity contenant la liste des annonces recommandées
     */
    public ResponseEntity<ResponseDTO<List<Annonce>>> recommandationUser(Long userId, String type, List<Long> excludeIds) {
        ResponseDTO<List<Annonce>> response = new ResponseDTO<>();
        try {
            long totalAnnonces = annonceRepository.count();
            long totalInteractions = interactionRepository.count();

            if (totalAnnonces < 100 || totalInteractions < 100) {
                List<Annonce> randomAnnonces;
                if (!"general".equals(type)) {
                    randomAnnonces = annonceRepository.findByVehicleType(type);
                } else {
                    randomAnnonces = annonceRepository.findAll();
                }

                // Filtrer les annonces déjà recommandées
                if (excludeIds != null && !excludeIds.isEmpty()) {
                    randomAnnonces = randomAnnonces.stream()
                        .filter(annonce -> !excludeIds.contains(annonce.getAnnonceId()))
                        .collect(Collectors.toList());
                }

                // Si moins de 12 annonces disponibles, retourner une liste vide
                if (randomAnnonces.size() < 12) {
                    response.setMessage("Il n'y a plus d'annonces à recommander");
                    response.setData(new ArrayList<>());
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }

                Collections.shuffle(randomAnnonces);
                response.setData(randomAnnonces.stream().limit(12).collect(Collectors.toList()));
                response.setMessage("Recommandations aléatoires récupérées avec succès");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

            // Configuration pour l'appel à l'API
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Créer le corps de la requête avec les IDs à exclure
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("excludeIds", excludeIds != null ? excludeIds : new ArrayList<>());
            
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            // Sélection de l'endpoint
            String endpoint = "http://localhost:8000/recommend/";
            endpoint += "voiture".equals(type) ? "cars/" : 
                       "moto".equals(type) ? "motos/" : "general/";
            endpoint += userId;

            // Appel à l'API
            ResponseEntity<List<Integer>> responseEntity = restTemplate.exchange(
                endpoint,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<List<Integer>>() {}
            );

            // Convertir les IDs en annonces
            List<Annonce> recommendedAnnonces = new ArrayList<>();
            List<Integer> annonceIds = responseEntity.getBody();

            if (annonceIds != null && !annonceIds.isEmpty()) {
                for (Integer annonceId : annonceIds) {
                    Optional<Annonce> annonceOpt = annonceRepository.findById(annonceId.longValue());
                    annonceOpt.ifPresent(recommendedAnnonces::add);
                }
                response.setMessage("Recommandations récupérées avec succès");
            } else {
                // Si la liste est vide, retourner 12 annonces au hasard
                List<Annonce> allAnnonces = annonceRepository.findAll();
                Collections.shuffle(allAnnonces);
                recommendedAnnonces = allAnnonces.stream()
                        .limit(12)
                        .collect(Collectors.toList());
                response.setMessage("Recommandation vide, annonces aléatoires récupérées avec succès");
            }

            // Après l'appel à l'API Python, vérifier le nombre d'annonces
            if (recommendedAnnonces.size() < 12) {
                response.setMessage("Il n'y a plus d'annonces à recommander");
                response.setData(new ArrayList<>());
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

            response.setData(recommendedAnnonces);
            response.setMessage("Recommandations récupérées avec succès");
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (RestClientException e) {
            e.printStackTrace();
            response.setMessage("Erreur lors de la communication avec le service de recommandation: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage(
                    "Une erreur est survenue lors de la récupération des recommandations: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère toutes les annonces associées à un utilisateur spécifique, incluant
     * les détails de l'utilisateur.
     * 
     * @param userId Identifiant unique de l'utilisateur
     * @return ResponseEntity contenant:
     *         - 200 OK: Liste des annonces de l'utilisateur avec message de succès
     *         et détails de l'utilisateur
     *         - 204 NO_CONTENT: Si l'utilisateur n'a pas d'annonces
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    public ResponseEntity<ResponseDTO<List<AnnonceWithUserDTO>>> getAnnoncesByUserId(Long userId) {
        ResponseDTO<List<AnnonceWithUserDTO>> response = new ResponseDTO<>();
        try {
            List<Annonce> annonces = annonceRepository.findByVendorId(userId);
            if (annonces.isEmpty()) {
                response.setMessage("Aucune annonce trouvée pour l'utilisateur avec l'ID: " + userId);
                return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
            }
            List<AnnonceWithUserDTO> annonceDTOs = annonces.stream()
                    .map(annonce -> new AnnonceWithUserDTO(annonce, annonce.getVendor())) // Assurez-vous que AnnonceDTO
                                                                                          // prend Annonce et User
                    .collect(Collectors.toList());
            response.setMessage("Les annonces de l'utilisateur ont été récupérées avec succès");
            response.setData(annonceDTOs);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Une erreur est survenue lors de la récupération des annonces de l'utilisateur");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Crée une nouvelle annonce dans le système.
     * Vérifie l'existence du véhicule associé et initialise les timestamps.
     * 
     * @param annonceCreationDTO Détails de l'annonce qui va être créé
     * @return ResponseEntity contenant:
     *         - 201 CREATED: L'annonce créée avec message de succès
     *         - 404 NOT_FOUND: Si l'utilisateur n'a pas été trouvés
     *         - 400 BAD_REQUEST: Si le véhicule spécifié n'existe pas
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    public ResponseEntity<ResponseDTO<AnnonceWithUserDTO>> createAnnonce(AnnonceCreationDTO annonceCreationDTO) {
        ResponseDTO<AnnonceWithUserDTO> response = new ResponseDTO<>();
        try {
            // Création du véhicule
            ResponseEntity<ResponseDTO<Vehicle>> vehicleResponse = vehicleService
                    .createVehicle(annonceCreationDTO.getVehicle());
            if (vehicleResponse.getStatusCode() != HttpStatus.CREATED) {
                response.setMessage("Le véhicule spécifié n'a pas pu être créé");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            ResponseDTO<Vehicle> vehicleResponseDto = vehicleResponse.getBody();
            Vehicle vehicle;

            if (vehicleResponseDto == null || vehicleResponseDto.getData() == null) {
                response.setMessage("Le véhicule spécifié n'a pas pu être créé");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else {
                vehicle = vehicleResponseDto.getData();
            }

            // Importation de l'utilisateur pour la création d'annonce
            Optional<User> userOptional = userRepository.findById(annonceCreationDTO.getAnnonce().getUserId());
            if (!userOptional.isPresent()) {
                response.setMessage("L'utilisateur n'a pas été trouvé");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            User user = userOptional.get();

            // Creation de l'annonce
            Annonce annonce = new Annonce();
            annonce.setTitle(annonceCreationDTO.getAnnonce().getTitle());
            annonce.setPrice(annonceCreationDTO.getAnnonce().getPrice());
            annonce.setQuantity(annonceCreationDTO.getAnnonce().getQuantity());
            annonce.setTransaction(annonceCreationDTO.getAnnonce().getTransaction());
            annonce.setCity(annonceCreationDTO.getAnnonce().getCity());
            annonce.setPhoneNumber(annonceCreationDTO.getAnnonce().getPhoneNumber());
            annonce.setAnnonceState(AnnonceState.ACTIVE);
            annonce.setVendor(user);
            annonce.setVehicle(vehicle);

            Annonce savedAnnonce = annonceRepository.save(annonce);

            // Gestion des images
            for (String imageUrl : annonceCreationDTO.getImages()) {
                imageService.saveImage(imageUrl, savedAnnonce.getAnnonceId());
            }

            // Réponse avec AnnonceWithUserDTO
            AnnonceWithUserDTO annonceWithUserDTO = new AnnonceWithUserDTO(savedAnnonce, user);
            response.setMessage("L'annonce a été créée avec succès");
            response.setData(annonceWithUserDTO);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Une erreur est survenue lors de la création de l'annonce");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Met à jour une annonce existante avec de nouvelles informations.
     * Vérifie l'existence de l'annonce et du véhicule associé avant la mise à jour.
     * 
     * @param id             Identifiant unique de l'annonce à modifier
     * @param updatedAnnonce Objet Annonce contenant les nouvelles informations
     * @return ResponseEntity contenant:
     *         - 200 OK: L'annonce mise à jour avec message de succès
     *         - 400 BAD_REQUEST: Si le véhicule spécifié n'existe pas
     *         - 404 NOT_FOUND: Si l'annonce à modifier n'existe pas
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    public ResponseEntity<ResponseDTO<AnnonceWithUserDTO>> updateAnnonce(Long id, AnnonceUpdateDTO annonceUpdateDTO) {
        ResponseDTO<AnnonceWithUserDTO> response = new ResponseDTO<>();
        try {
            // Vérification de l'existence de l'annonce
            Optional<Annonce> existingAnnonce = annonceRepository.findById(id);
            if (!existingAnnonce.isPresent()) {
                response.setMessage("Aucune annonce trouvée avec l'ID: " + id);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // Mise à jour du véhicule uniquement si des informations sont fournies
            if (annonceUpdateDTO.getVehicle() != null) {
                ResponseEntity<ResponseDTO<Vehicle>> vehicleResponse = vehicleService
                        .updateVehicle(existingAnnonce.get().getVehicle().getVehicleId(),
                                annonceUpdateDTO.getVehicle());
                if (vehicleResponse.getStatusCode() != HttpStatus.OK) {
                    response.setMessage("Le véhicule spécifié n'a pas pu être mis à jour");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
            }

            // Mise à jour des champs de l'annonce uniquement si des informations sont
            // fournies
            Annonce annonce = existingAnnonce.get();
            AnnonceDetailsUpdateDTO annonceDetails = annonceUpdateDTO.getAnnonce();

            if (annonceDetails.getTitle() != null) {
                annonce.setTitle(annonceDetails.getTitle());
            }
            if (annonceDetails.getPrice() != null) {
                annonce.setPrice(annonceDetails.getPrice());
            }
            if (annonceDetails.getQuantity() != null) {
                annonce.setQuantity(annonceDetails.getQuantity());
            }
            if (annonceDetails.getTransaction() != null) {
                annonce.setTransaction(annonceDetails.getTransaction());
            }
            if (annonceDetails.getCity() != null) {
                annonce.setCity(annonceDetails.getCity());
            }
            if (annonceDetails.getPhoneNumber() != null) {
                annonce.setPhoneNumber(annonceDetails.getPhoneNumber());
            }

            if (annonceDetails.getAnnonceState() != null) {
                annonce.setAnnonceState(annonceDetails.getAnnonceState());
            }

            annonce.setUpdatedAt(LocalDateTime.now());

            Annonce savedAnnonce = annonceRepository.save(annonce);

            // Mise à jour des images uniquement si de nouvelles images sont fournies
            if (annonceUpdateDTO.getImages() != null && annonceUpdateDTO.getImages().length > 0) {
                imageService.deleteImagesByAnnonceId(id);
                for (String imageUrl : annonceUpdateDTO.getImages()) {
                    imageService.saveImage(imageUrl, savedAnnonce.getAnnonceId());
                }
            }

            // Récupération de l'utilisateur associé à l'annonce
            User user = savedAnnonce.getVendor();
            AnnonceWithUserDTO annonceWithUserDTO = new AnnonceWithUserDTO(savedAnnonce, user);

            response.setMessage("L'annonce a été mise à jour avec succès");
            response.setData(annonceWithUserDTO);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Une erreur est survenue lors de la mise à jour de l'annonce");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Supprime une annonce spécifique du système.
     * 
     * @param id Identifiant unique de l'annonce à supprimer
     * @return ResponseEntity contenant:
     *         - 200 OK: Message de confirmation de suppression
     *         - 404 NOT_FOUND: Si l'annonce à supprimer n'existe pas
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    public ResponseEntity<ResponseDTO<Void>> deleteAnnonce(Long id) {
        ResponseDTO<Void> response = new ResponseDTO<>();
        try {
            if (!annonceRepository.existsById(id)) {
                response.setMessage("Aucune annonce trouvée avec l'ID: " + id);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            annonceRepository.deleteById(id);
            response.setMessage("L'annonce a été supprimée avec succès");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Une erreur est survenue lors de la suppression de l'annonce");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Recherche des annonces par mot-clé dans le titre.
     * 
     * @param keyword Mot-clé à rechercher dans les annonces
     * @return ResponseEntity contenant:
     *         - 200 OK: Liste des annonces correspondantes avec message de succès
     *         - 204 NO_CONTENT: Si aucune annonce ne correspond au mot-clé
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    public ResponseEntity<ResponseDTO<List<Annonce>>> searchAnnonces(String keyword) {
        ResponseDTO<List<Annonce>> response = new ResponseDTO<>();
        try {
            List<Annonce> annonces = annonceRepository.findByTitleContaining(keyword);
            if (annonces.isEmpty()) {
                response.setMessage("Aucune annonce trouvée avec le mot-clé: " + keyword);
                return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
            }
            response.setMessage("Les annonces ont été trouvées avec succès");
            response.setData(annonces);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Une erreur est survenue lors de la recherche des annonces");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère les annonces par état de transaction.
     * 
     * @param transaction État de transaction des annonces à rechercher
     * @return ResponseEntity contenant:
     *         - 200 OK: Liste des annonces de la transaction avec message de succès
     *         - 204 NO_CONTENT: Si aucune annonce n'existe pour cette transaction
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    public ResponseEntity<ResponseDTO<List<Annonce>>> getAnnoncesByTransaction(String transaction) {
        ResponseDTO<List<Annonce>> response = new ResponseDTO<>();
        try {
            Optional<List<Annonce>> annoncesOpt = annonceRepository.findByTransaction(transaction);
            if (!annoncesOpt.isPresent() || annoncesOpt.get().isEmpty()) {
                response.setMessage("Aucune annonce trouvée pour la transaction: " + transaction);
                return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
            }
            response.setMessage("Les annonces ont été trouvées avec succès");
            response.setData(annoncesOpt.get());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Une erreur est survenue lors de la récupération des annonces par transaction");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère les annonces par localisation.
     * 
     * @param country Pays des annonces à rechercher
     * @param city    Ville des annonces à rechercher
     * @return ResponseEntity contenant:
     *         - 200 OK: Liste des annonces pour la localisation avec message de
     *         succès
     *         - 204 NO_CONTENT: Si aucune annonce n'existe pour cette localisation
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    public ResponseEntity<ResponseDTO<List<Annonce>>> getAnnoncesByLocation(String country, String city) {
        ResponseDTO<List<Annonce>> response = new ResponseDTO<>();
        try {
            List<Annonce> annonces;
            if (city != null && !city.isEmpty()) {
                annonces = annonceRepository.findByCity(city);
            } else {
                annonces = annonceRepository.findByCountry(country);
            }

            if (annonces.isEmpty()) {
                response.setMessage("Aucune annonce trouvée à l'emplacement spécifié");
                return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
            }
            response.setMessage("Les annonces ont été trouvées avec succès");
            response.setData(annonces);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Une erreur est survenue lors de la récupération des annonces par emplacement");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère l'annonce premium associée à un identifiant donné.
     * 
     * @param id Identifiant unique de l'annonce premium à récupérer.
     * @return ResponseEntity<ResponseDTO<PremiumAnnonceDTO>> contenant l'annonce
     *         premium
     *         ou un message d'erreur si l'annonce n'est pas trouvée.
     */
    public ResponseEntity<ResponseDTO<PremiumAnnonceDTO>> getPremiumAnnonce(Long id) {
        ResponseDTO<PremiumAnnonceDTO> response = new ResponseDTO<>();
        Optional<Annonce> premiumAnnonce = annonceRepository.findPremiumAnnonceById(id);

        if (premiumAnnonce.isPresent()) {
            PremiumAnnonceDTO dto = new PremiumAnnonceDTO();
            dto.setAnnonceId(premiumAnnonce.get().getAnnonceId());
            dto.setTitle(premiumAnnonce.get().getTitle());
            dto.setPremium(true);

            response.setData(dto);
            response.setMessage("Annonce premium récupérée avec succès");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setMessage("Aucune annonce premium trouvée avec l'ID: " + id);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Filtre les annonces selon divers critères spécifiés.
     * 
     * @param userId         Identifiant de l'utilisateur qui a créé l'annonce.
     * @param annonceState   État de l'annonce (ex: actif, inactif).
     * @param title          Titre de l'annonce à rechercher.
     * @param premium        Indicateur pour filtrer les annonces premium.
     * @param vehicles       Types de véhicules à inclure dans le filtre.
     * @param sort           Critère de tri pour les résultats.
     * @param transaction    État de la transaction des annonces à rechercher.
     * @param annonce        Autres critères d'annonce à filtrer.
     * @param minKilometrage Kilométrage minimum des véhicules.
     * @param maxKilometrage Kilométrage maximum des véhicules.
     * @param minPrice       Prix minimum des annonces.
     * @param maxPrice       Prix maximum des annonces.
     * @param city           Ville des annonces à rechercher.
     * @param marque         Marque des véhicules à rechercher.
     * @return ResponseEntity<ResponseDTO<List<Annonce>>> contenant la liste des
     *         annonces filtrées
     *         ou un message d'erreur si aucune annonce ne correspond aux critères.
     */
    public ResponseEntity<ResponseDTO<List<Annonce>>> filterAnnonces(
            Long vendorId, String annonceState, String title, Boolean premium,
            String vehicles, String sort, String transaction, String annonce,
            Integer minKilometrage, Integer maxKilometrage,
            Double minPrice, Double maxPrice, String city, String marque) {

        ResponseDTO<List<Annonce>> response = new ResponseDTO<>();
        try {
            Optional<List<Annonce>> filteredAnnonces = annonceRepository.filterAnnonces(
                    vendorId,
                    premium,
                    transaction,
                    annonceState,
                    city,
                    minKilometrage,
                    maxKilometrage,
                    minPrice,
                    maxPrice,
                    marque);

            if (filteredAnnonces.isPresent() && filteredAnnonces.get().isEmpty()) {
                response.setMessage("Aucune annonce trouvée avec les critères spécifiés");
                return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
            }
            response.setMessage("Annonces filtrées avec succès");
            response.setData(filteredAnnonces.orElse(Collections.emptyList()));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Une erreur est survenue lors du filtrage des annonces");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}