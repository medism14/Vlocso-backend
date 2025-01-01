package com.vlosco.backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import com.vlosco.backend.utils.SearchUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Service gérant les annonces de véhicules.
 * Fournit les opérations CRUD et des fonctionnalités de recherche avancée pour
 * les annonces.
 * Gère la validation des données, les relations avec les véhicules et le
 * traitement des erreurs.
 */
@Service
public class AnnonceService {

    private static final Logger log = LoggerFactory.getLogger(AnnonceService.class);

    private final AnnonceRepository annonceRepository;
    private final RestTemplate restTemplate;

    private final VehicleService vehicleService;
    private final InteractionRepository interactionRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;

    @Value("${fastapi.url}")
    private String fastapiUrl;

    @Autowired
    private NotificationService notificationService;

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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
    public ResponseEntity<ResponseDTO<List<AnnonceWithUserDTO>>> recommandationUser(Long userId, String type,
            Integer nbAnnonces,
            List<Long> excludeIds) {
        ResponseDTO<List<AnnonceWithUserDTO>> response = new ResponseDTO<>();
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

                // Si moins d'annonces disponibles que demandé, retourner une liste vide
                if (randomAnnonces.size() < nbAnnonces) {
                    response.setMessage("Il n'y a plus d'annonces à recommander");
                    response.setData(new ArrayList<>());
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }

                Collections.shuffle(randomAnnonces);
                // Convertir en AnnonceWithUserDTO
                List<AnnonceWithUserDTO> annonceDTOs = randomAnnonces.stream()
                        .limit(nbAnnonces)
                        .map(annonce -> new AnnonceWithUserDTO(annonce, annonce.getVendor()))
                        .collect(Collectors.toList());

                response.setData(annonceDTOs);
                response.setMessage("Recommandations aléatoires récupérées avec succès");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

            // Configuration pour l'appel à l'API
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Créer le corps de la requête avec les IDs à exclure
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("excludeIds", excludeIds != null ? excludeIds : new ArrayList<>());
            requestBody.put("nbAnnonces", nbAnnonces);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            String endpoint = fastapiUrl
                    + (type.equals("voitures") ? "voitures/" : type.equals("motos") ? "motos/" : "general/") + userId;

            // Appel à l'API
            ResponseEntity<List<Integer>> responseEntity = restTemplate.exchange(
                    endpoint,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<List<Integer>>() {
                    });

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
            if (recommendedAnnonces.size() < nbAnnonces) {
                response.setMessage("Il n'y a plus d'annonces à recommander");
                response.setData(new ArrayList<>());
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // Convertir la liste d'Annonce en AnnonceWithUserDTO
            List<AnnonceWithUserDTO> recommendedAnnonceDTOs = recommendedAnnonces.stream()
                    .map(annonce -> new AnnonceWithUserDTO(annonce, annonce.getVendor()))
                    .collect(Collectors.toList());

            response.setData(recommendedAnnonceDTOs);
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
    @Transactional(readOnly = true)
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
    @Transactional
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
    @Transactional
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
    @Transactional
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
     * @param searchText  Texte à rechercher dans les annonces
     * @param excludedIds IDs des annonces à exclure
     * @param nbAnnonces  Nombre d'annonces à retourner
     * @return ResponseEntity contenant:
     *         - 200 OK: Liste des annonces correspondantes avec message de succès
     *         - 204 NO_CONTENT: Si aucune annonce ne correspond au mot-clé
     *         - 500 INTERNAL_SERVER_ERROR: En cas d'erreur technique
     */
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseDTO<List<AnnonceWithUserDTO>>> searchAnnonces(String searchText, String[] excludedIds,
            Integer nbAnnonces, String sort, String filterTransaction, Integer minKilometrage, Integer maxKilometrage,
            Double minPrice, Double maxPrice, String city, String filterMark, String filterModel) {
        ResponseDTO<List<AnnonceWithUserDTO>> response = new ResponseDTO<>();
        try {
            Map<String, String> searchCriteria = SearchUtils.analyzeSearchText(searchText != null ? searchText : "");

            String transaction = filterTransaction != null ? filterTransaction
                    : searchCriteria.get("annonce.transaction");
            String type = searchCriteria.get("vehicle.type");
            String mark = filterMark != null ? filterMark : searchCriteria.get("vehicle.mark");
            String model = filterModel != null ? filterModel : searchCriteria.get("vehicle.model");
            String color = searchCriteria.get("vehicle.color");
            String category = searchCriteria.get("vehicle.category");
            String fuelType = searchCriteria.get("vehicle.fuelType");
            Integer year = searchCriteria.get("vehicle.year") != null
                    ? Integer.valueOf(searchCriteria.get("vehicle.year"))
                    : null;
            Integer limit = nbAnnonces != null ? nbAnnonces : 20;
            Set<Annonce> allAnnonces = new LinkedHashSet<>();

            // Premier essai avec tous les critères
            List<Annonce> annonces = executeRequest(sort, type, transaction, mark, model, category, fuelType, color, city, year, minKilometrage,
                    maxKilometrage, minPrice, maxPrice, excludedIds, limit);
            allAnnonces.addAll(annonces);

            // Si on n'a pas atteint la limite, on fait des recherches progressivement plus
            // larges
            if (allAnnonces.size() < limit) {
                // Mise à jour des IDs exclus
                String[] newExcludedIds = allAnnonces.stream()
                        .map(a -> a.getAnnonceId().toString())
                        .toArray(String[]::new);

                // Recherche sans l'année
                if (year != null) {
                    year = null;
                    annonces = executeRequest(sort, type, transaction, mark, model, category, fuelType, color, city, year, minKilometrage,
                            maxKilometrage, minPrice, maxPrice, newExcludedIds, limit - allAnnonces.size());
                    allAnnonces.addAll(annonces);
                }

                // Recherche sans la couleur
                if (allAnnonces.size() < limit && color != null) {
                    newExcludedIds = allAnnonces.stream()
                            .map(a -> a.getAnnonceId().toString())
                            .toArray(String[]::new);
                    color = null;
                    annonces = executeRequest(sort, type, transaction, mark, model, category, fuelType, color, city, year, minKilometrage,
                            maxKilometrage, minPrice, maxPrice, newExcludedIds, limit - allAnnonces.size());
                    allAnnonces.addAll(annonces);
                }

                // Recherche sans le modèle (si pas filtré explicitement)
                if (allAnnonces.size() < limit && model != null && filterModel == null) {
                    newExcludedIds = allAnnonces.stream()
                            .map(a -> a.getAnnonceId().toString())
                            .toArray(String[]::new);
                    model = null;
                    annonces = executeRequest(sort, type, transaction, mark, model, category, fuelType, color, city, year, minKilometrage,
                            maxKilometrage, minPrice, maxPrice, newExcludedIds, limit - allAnnonces.size());
                    allAnnonces.addAll(annonces);
                }

                // Recherche sans la catégorie
                if (allAnnonces.size() < limit && category != null) {
                    newExcludedIds = allAnnonces.stream()
                            .map(a -> a.getAnnonceId().toString())
                            .toArray(String[]::new);
                    category = null;
                    annonces = executeRequest(sort, type, transaction, mark, model, category, fuelType, color, city, year, minKilometrage,
                            maxKilometrage, minPrice, maxPrice, newExcludedIds, limit - allAnnonces.size());
                    allAnnonces.addAll(annonces);
                }

                // Recherche sans le type de carburant
                if (allAnnonces.size() < limit && fuelType != null) {
                    newExcludedIds = allAnnonces.stream()
                            .map(a -> a.getAnnonceId().toString())
                            .toArray(String[]::new);
                    fuelType = null;
                    annonces = executeRequest(sort, type, transaction, mark, model, category, fuelType, color, city, year, minKilometrage,
                            maxKilometrage, minPrice, maxPrice, newExcludedIds, limit - allAnnonces.size());
                    allAnnonces.addAll(annonces);
                }

                // Recherche sans la marque (si pas filtrée explicitement)
                if (allAnnonces.size() < limit && mark != null && filterMark == null) {
                    newExcludedIds = allAnnonces.stream()
                            .map(a -> a.getAnnonceId().toString())
                            .toArray(String[]::new);
                    mark = null;
                    annonces = executeRequest(sort, type, transaction, mark, model, category, fuelType, color, city, year, minKilometrage,
                            maxKilometrage, minPrice, maxPrice, newExcludedIds, limit - allAnnonces.size());
                    allAnnonces.addAll(annonces);
                }

                // Recherche sans la transaction
                if (allAnnonces.size() < limit && transaction != null && filterTransaction == null) {
                    newExcludedIds = allAnnonces.stream()
                            .map(a -> a.getAnnonceId().toString())
                            .toArray(String[]::new);
                    transaction = null;
                    annonces = executeRequest(sort, type, transaction, mark, model, category, fuelType, color, city, year, minKilometrage,
                            maxKilometrage, minPrice, maxPrice, newExcludedIds, limit - allAnnonces.size());
                    allAnnonces.addAll(annonces);
                }

                // Si toujours pas assez, on prend les annonces populaires
                if (allAnnonces.size() < limit) {
                    newExcludedIds = allAnnonces.stream()
                            .map(a -> a.getAnnonceId().toString())
                            .toArray(String[]::new);
                    annonces = annonceRepository.findMostPopularAnnonces(limit, newExcludedIds);
                    allAnnonces.addAll(annonces);
                }
            }

            // Conversion en liste et conversion en DTO
            List<AnnonceWithUserDTO> finalAnnonceDTOs = allAnnonces.stream()
                    .map(annonce -> new AnnonceWithUserDTO(annonce, annonce.getVendor()))
                    .collect(Collectors.toList());

            response.setData(finalAnnonceDTOs);
            response.setMessage("Recherche effectuée avec succès");
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Une erreur est survenue lors de la recherche : " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<Annonce> executeRequest(String sort, String type, String transaction, String mark, String model, String category, 
            String fuelType, String color, String city, Integer year, Integer minKilometrage,
            Integer maxKilometrage, Double minPrice, Double maxPrice, String[] excludedIds, Integer limit) {
        
        if ("price_asc".equals(sort)) {
            return annonceRepository.searchAnnoncesAdvancedPriceAsc(type, transaction, mark, model, category, fuelType, color, city, year, minKilometrage,
                    maxKilometrage, minPrice, maxPrice, excludedIds, limit);
        } else if ("price_desc".equals(sort)) {
            return annonceRepository.searchAnnoncesAdvancedPriceDesc(type, transaction, mark, model, category, fuelType, color, city, year, minKilometrage,
                    maxKilometrage, minPrice, maxPrice, excludedIds, limit);
        } else if ("date_asc".equals(sort)) {
            return annonceRepository.searchAnnoncesAdvancedDateAsc(type, transaction, mark, model, category, fuelType, color, city, year, minKilometrage,
                    maxKilometrage, minPrice, maxPrice, excludedIds, limit);
        } else if ("date_desc".equals(sort)) {
            return annonceRepository.searchAnnoncesAdvancedDateDesc(type, transaction, mark, model, category, fuelType, color, city, year, minKilometrage,
                    maxKilometrage, minPrice, maxPrice, excludedIds, limit);
        } else {
            return annonceRepository.searchAnnoncesAdvanced(type, transaction, mark, model, category, fuelType, color, city, year, minKilometrage,
                    maxKilometrage, minPrice, maxPrice, excludedIds, limit);
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseDTO<List<Annonce>>> filterAnnonces(
            List<Long> annonceIds,
            Long vendorId,
            String annonceState,
            Boolean premium,
            String typeVehicle,
            String sort,
            String transaction,
            String annonce,
            Integer minKilometrage,
            Integer maxKilometrage,
            Double minPrice,
            Double maxPrice,
            String city,
            String marque,
            String model) {

        ResponseDTO<List<Annonce>> response = new ResponseDTO<>();
        try {
            List<Annonce> filteredAnnonces = annonceRepository.filterAnnonces(
                annonceIds,
                premium,
                transaction,
                city,
                minKilometrage,
                maxKilometrage,
                minPrice,
                maxPrice,
                marque,
                model,
                typeVehicle
            );

            if (filteredAnnonces.isEmpty()) {
                response.setMessage("Aucune annonce trouvée avec les critères spécifiés");
                return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
            }

            // Appliquer le tri si spécifié
            if (sort != null) {
                switch (sort.toLowerCase()) {
                    case "price_asc":
                        filteredAnnonces.sort((a1, a2) -> a1.getPrice().compareTo(a2.getPrice()));
                        break;
                    case "price_desc":
                        filteredAnnonces.sort((a1, a2) -> a2.getPrice().compareTo(a1.getPrice()));
                        break;
                    case "date_asc":
                        filteredAnnonces.sort((a1, a2) -> a1.getCreatedAt().compareTo(a2.getCreatedAt()));
                        break;
                    case "date_desc":
                        filteredAnnonces.sort((a1, a2) -> a2.getCreatedAt().compareTo(a1.getCreatedAt()));
                        break;
                }
            }

            response.setMessage("Annonces filtrées avec succès");
            response.setData(filteredAnnonces);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Une erreur est survenue lors du filtrage des annonces");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResponseDTO<List<AnnonceWithUserDTO>>> findSimilarAnnonces(Long annonceId,
            Integer nbAnnonces) {
        ResponseDTO<List<AnnonceWithUserDTO>> response = new ResponseDTO<>();
        try {
            Optional<Annonce> referenceAnnonce = annonceRepository.findById(annonceId);
            if (referenceAnnonce.isEmpty()) {
                response.setMessage("Annonce de référence non trouvée");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            Annonce reference = referenceAnnonce.get();
            Vehicle referenceVehicle = reference.getVehicle();

            // Récupérer les annonces actives du même type avec une seule requête optimisée
            List<Annonce> similarAnnonces = annonceRepository.findSimilarAnnonces(
                    referenceVehicle.getType(),
                    referenceVehicle.getMark(),
                    referenceVehicle.getModel(),
                    referenceVehicle.getCategory(),
                    referenceVehicle.getFuelType(),
                    referenceVehicle.getYear(),
                    reference.getPrice(),
                    annonceId,
                    reference.getCity());

            // Calculer les scores de similarité avec pondération
            Map<Annonce, Double> similarityScores = new HashMap<>();
            for (Annonce annonce : similarAnnonces) {
                double score = calculateSimilarityScore(reference, annonce);
                similarityScores.put(annonce, score);
            }

            // Trier par score de similarité
            List<Annonce> sortedAnnonces = similarityScores.entrySet().stream()
                    .sorted(Map.Entry.<Annonce, Double>comparingByValue().reversed())
                    .limit(nbAnnonces * 3)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            // Mélanger les meilleures annonces et prendre le nombre demandé
            Collections.shuffle(sortedAnnonces.subList(0, Math.min(sortedAnnonces.size(), nbAnnonces * 2)));
            List<Annonce> finalAnnonces = sortedAnnonces.stream()
                    .limit(nbAnnonces)
                    .collect(Collectors.toList());

            // Conversion en liste et conversion en DTO
            List<AnnonceWithUserDTO> finalAnnonceDTOs = finalAnnonces.stream()
                    .map(annonce -> new AnnonceWithUserDTO(annonce, annonce.getVendor()))
                    .collect(Collectors.toList());

            response.setData(finalAnnonceDTOs);
            response.setMessage("Annonces similaires trouvées avec succès");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setMessage("Erreur lors de la recherche d'annonces similaires: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private double calculateSimilarityScore(Annonce reference, Annonce other) {
        double score = 0.0;
        Vehicle refVehicle = reference.getVehicle();
        Vehicle otherVehicle = other.getVehicle();

        // Critères véhicule avec pondération
        if (refVehicle.getMark().equalsIgnoreCase(otherVehicle.getMark()))
            score += 25.0;
        if (refVehicle.getModel().equalsIgnoreCase(otherVehicle.getModel()))
            score += 20.0;
        if (refVehicle.getCategory().equalsIgnoreCase(otherVehicle.getCategory()))
            score += 15.0;
        if (refVehicle.getFuelType().equalsIgnoreCase(otherVehicle.getFuelType()))
            score += 10.0;
        if (refVehicle.getGearbox().equalsIgnoreCase(otherVehicle.getGearbox()))
            score += 5.0;
        if (refVehicle.getColor().equalsIgnoreCase(otherVehicle.getColor()))
            score += 3.0;

        // Proximité d'année (score dégressif)
        int yearDiff = Math.abs(refVehicle.getYear() - otherVehicle.getYear());
        if (yearDiff == 0)
            score += 10.0;
        else if (yearDiff <= 2)
            score += 7.0;
        else if (yearDiff <= 5)
            score += 4.0;

        // Proximité de kilométrage (score dégressif)
        int kmDiff = Math.abs(refVehicle.getKlmCounter() -
                otherVehicle.getKlmCounter());
        if (kmDiff < 10000)
            score += 5.0;
        else if (kmDiff < 30000)
            score += 3.0;
        else if (kmDiff < 50000)
            score += 1.0;

        // Proximité de prix
        double priceDiff = Math.abs(reference.getPrice() -
                other.getPrice());
        double refPrice = reference.getPrice();
        if (priceDiff < refPrice * 0.1)
            score += 5.0;
        else if (priceDiff < refPrice * 0.2)
            score += 3.0;
        else if (priceDiff < refPrice * 0.3)
            score += 1.0;

        // Bonus pour même ville/région
        if (reference.getCity().equalsIgnoreCase(other.getCity()))
            score += 2.0;

        return score;
    }

    public ResponseEntity<ResponseDTO<List<HashMap<String, List<AnnonceWithUserDTO>>>>> recommandationUserNotConnected(
            Integer nbAnnonces) {
        ResponseDTO<List<HashMap<String, List<AnnonceWithUserDTO>>>> response = new ResponseDTO<>();
        try {
            if (nbAnnonces == null || nbAnnonces <= 0) {
                nbAnnonces = 12;
            }

            // Récupération des annonces générales (60% voitures, 40% motos)
            int nbVoitures = (int) Math.ceil(nbAnnonces * 0.6);
            int nbMotos = nbAnnonces - nbVoitures;

            // Récupérer les annonces populaires/aléatoires pour chaque type
            List<Annonce> recommandationGeneralesVoiture = annonceRepository.findPopularAnnonces(
                    "Voiture",
                    nbVoitures);

            // Conversion en DTO pour les voitures
            List<AnnonceWithUserDTO> recommandationGeneralesVoitureDTOs = recommandationGeneralesVoiture.stream()
                    .map(annonce -> new AnnonceWithUserDTO(annonce, annonce.getVendor()))
                    .collect(Collectors.toList());

            // Si on n'a pas assez d'annonces populaires, on complète avec des annonces
            // aléatoires
            if (recommandationGeneralesVoitureDTOs.size() < nbVoitures) {
                List<Long> excludedIds = recommandationGeneralesVoitureDTOs.stream()
                        .map(dto -> dto.getAnnonce().getAnnonceId())
                        .collect(Collectors.toList());
                int remaining = nbVoitures - recommandationGeneralesVoitureDTOs.size();
                List<Annonce> additionalVoitures = annonceRepository.findPopularAnnonces(
                        "Voiture",
                        excludedIds,
                        remaining);
                List<AnnonceWithUserDTO> additionalVoituresDTOs = additionalVoitures.stream()
                        .map(annonce -> new AnnonceWithUserDTO(annonce, annonce.getVendor()))
                        .collect(Collectors.toList());
                recommandationGeneralesVoitureDTOs.addAll(additionalVoituresDTOs);
            }

            List<Annonce> recommandationGeneralesMoto = annonceRepository.findPopularAnnonces(
                    "Moto",
                    nbMotos);

            // Conversion en DTO pour les motos
            List<AnnonceWithUserDTO> recommandationGeneralesMotoDTOs = recommandationGeneralesMoto.stream()
                    .map(annonce -> new AnnonceWithUserDTO(annonce, annonce.getVendor()))
                    .collect(Collectors.toList());

            // Même chose pour les motos
            if (recommandationGeneralesMotoDTOs.size() < nbMotos) {
                List<Long> excludedIds = recommandationGeneralesMotoDTOs.stream()
                        .map(dto -> dto.getAnnonce().getAnnonceId())
                        .collect(Collectors.toList());
                int remaining = nbMotos - recommandationGeneralesMotoDTOs.size();
                List<Annonce> additionalMotos = annonceRepository.findPopularAnnonces(
                        "Moto",
                        excludedIds,
                        remaining);
                List<AnnonceWithUserDTO> additionalMotosDTOs = additionalMotos.stream()
                        .map(annonce -> new AnnonceWithUserDTO(annonce, annonce.getVendor()))
                        .collect(Collectors.toList());
                recommandationGeneralesMotoDTOs.addAll(additionalMotosDTOs);
            }

            // Création des IDs à exclure pour les recommandations spécifiques
            List<Long> excludedIds = new ArrayList<>();
            recommandationGeneralesVoitureDTOs.forEach(dto -> excludedIds.add(dto.getAnnonce().getAnnonceId()));
            recommandationGeneralesMotoDTOs.forEach(dto -> excludedIds.add(dto.getAnnonce().getAnnonceId()));

            // Création de la liste de hashmaps pour la réponse
            List<HashMap<String, List<AnnonceWithUserDTO>>> recommendations = new ArrayList<>();

            // HashMap pour les recommandations générales
            HashMap<String, List<AnnonceWithUserDTO>> generalMap = new HashMap<>();
            List<AnnonceWithUserDTO> generalList = new ArrayList<>();
            generalList.addAll(recommandationGeneralesVoitureDTOs);
            generalList.addAll(recommandationGeneralesMotoDTOs);
            Collections.shuffle(generalList);
            generalMap.put("general", generalList);
            recommendations.add(generalMap);

            // Récupération et conversion des recommandations spécifiques par type
            List<Annonce> recommandationVoitures = annonceRepository.findPopularAnnonces(
                    "Voiture",
                    excludedIds,
                    nbAnnonces);
            List<AnnonceWithUserDTO> recommandationVoituresDTOs = recommandationVoitures.stream()
                    .map(annonce -> new AnnonceWithUserDTO(annonce, annonce.getVendor()))
                    .collect(Collectors.toList());

            // HashMap pour les voitures
            HashMap<String, List<AnnonceWithUserDTO>> voituresMap = new HashMap<>();
            voituresMap.put("voitures", recommandationVoituresDTOs);
            recommendations.add(voituresMap);

            List<Annonce> recommandationMotos = annonceRepository.findPopularAnnonces(
                    "Moto",
                    excludedIds,
                    nbAnnonces);
            List<AnnonceWithUserDTO> recommandationMotosDTOs = recommandationMotos.stream()
                    .map(annonce -> new AnnonceWithUserDTO(annonce, annonce.getVendor()))
                    .collect(Collectors.toList());

            // HashMap pour les motos
            HashMap<String, List<AnnonceWithUserDTO>> motosMap = new HashMap<>();
            motosMap.put("motos", recommandationMotosDTOs);
            recommendations.add(motosMap);

            response.setData(recommendations);
            response.setMessage("Recommandations récupérées avec succès");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.setMessage("Erreur lors de la récupération des recommandations: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Vérifie les annonces pour:
     * - Marquer comme expirées celles qui ont dépassé leur date de fin
     * - Notifier les vendeurs une semaine avant l'expiration
     * Utilise CRON: "0 0 * * * *" signifie:
     * - 0 secondes
     * - 0 minutes
     * - toutes les heures
     * - tous les jours
     * - tous les mois
     * - tous les jours de la semaine
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void checkAndNotifyAnnonces() {
        log.info("Vérification des annonces...");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekFromNow = now.plusWeeks(1);

        try {
            // Récupère les annonces expirées
            List<Annonce> expiredAnnonces = annonceRepository.findByAnnonceStateAndEndDateBefore(
                AnnonceState.ACTIVE, 
                now
            );

            // Traite les annonces expirées
            if (!expiredAnnonces.isEmpty()) {
                for (Annonce annonce : expiredAnnonces) {
                    annonce.setAnnonceState(AnnonceState.EXPIRED);
                    annonceRepository.save(annonce);

                    // Utilisation du NotificationService
                    notificationService.createNotification(
                        annonce.getVendor().getUserId(),
                        annonce.getAnnonceId(),
                        "Annonce expirée",
                        "Votre annonce \"" + annonce.getTitle() + "\" a expiré.",
                        false,
                        null
                    );
                }
                log.info("{} annonces expirées ont été mises à jour", expiredAnnonces.size());
            }

            // Récupère les annonces qui expirent dans une semaine
            List<Annonce> expiringAnnonces = annonceRepository.findByAnnonceStateAndEndDateBetween(
                AnnonceState.ACTIVE,
                now,
                oneWeekFromNow
            );

            // Notifie pour les annonces qui vont expirer
            if (!expiringAnnonces.isEmpty()) {
                for (Annonce annonce : expiringAnnonces) {
                    notificationService.createNotification(
                        annonce.getVendor().getUserId(),
                        annonce.getAnnonceId(),
                        "Annonce bientôt expirée",
                        "Votre annonce \"" + annonce.getTitle() + "\" expirera dans une semaine.",
                        false,
                        annonce.getEndDate()
                    );
                }
                log.info("{} notifications d'expiration prochaine envoyées", expiringAnnonces.size());
            }

        } catch (Exception e) {
            log.error("Erreur lors de la vérification des annonces: {}", e.getMessage());
        }
    }

    /**
     * Vérifie quotidiennement à minuit les annonces premium dont le paiement a expiré
     * et les notifications à envoyer pour les paiements qui vont bientôt expirer
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void checkPremiumPaymentsStatus() {
        log.info("Vérification des paiements premium des annonces...");
        LocalDateTime now = LocalDateTime.now();

        try {
            // Récupérer les annonces premium dont le paiement est expiré
            List<Annonce> expiredPremiumAnnonces = annonceRepository.findPremiumAnnoncesWithExpiredPayment(now);

            if (!expiredPremiumAnnonces.isEmpty()) {
                for (Annonce annonce : expiredPremiumAnnonces) {
                    // Désactiver le statut premium
                    annonce.setPremium(false);
                    annonceRepository.save(annonce);

                    // Notifier le vendeur
                    notificationService.createNotification(
                        annonce.getVendor().getUserId(),
                        annonce.getAnnonceId(),
                        "Statut premium expiré",
                        "Le statut premium de votre annonce \"" + annonce.getTitle() + "\" a expiré. " +
                        "Vous pouvez effectuer un nouveau paiement pour maintenir une meilleure visibilité.",
                        false,
                        null
                    );
                }
                log.info("{} annonces premium ont été désactivées", expiredPremiumAnnonces.size());
            }
        } catch (Exception e) {
            log.error("Erreur lors de la vérification des paiements premium: {}", e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<ResponseDTO<List<String>>> getAllCities() {
        ResponseDTO<List<String>> response = new ResponseDTO<>();
        try {
            List<String> cities = annonceRepository.findAllCities();
            
            if (cities.isEmpty()) {
                response.setMessage("Aucune ville trouvée");
                return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
            }

            // Trier les villes par ordre alphabétique
            Collections.sort(cities);
            
            response.setData(cities);
            response.setMessage("Liste des villes récupérée avec succès");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setMessage("Erreur lors de la récupération des villes : " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}