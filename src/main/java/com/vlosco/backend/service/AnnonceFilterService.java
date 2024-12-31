package com.vlosco.backend.service;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.vlosco.backend.dto.AnnonceFilterDTO;
import com.vlosco.backend.dto.ResponseDTO;
import com.vlosco.backend.model.Annonce;
import com.vlosco.backend.repository.AnnonceRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnnonceFilterService {

    private final AnnonceRepository annonceRepository;

    public AnnonceFilterService(AnnonceRepository annonceRepository) {
        this.annonceRepository = annonceRepository;
    }

    public ResponseEntity<ResponseDTO<List<Annonce>>> filterAnnonces(AnnonceFilterDTO filterDTO) {
        ResponseDTO<List<Annonce>> response = new ResponseDTO<>();
        try {
            // Validation des entrées
            if (filterDTO.getAnnonceIds() == null || filterDTO.getAnnonceIds().isEmpty()) {
                response.setMessage("La liste d'annonces ne peut pas être vide");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // Récupérer les annonces avec une seule requête optimisée
            List<Annonce> annonces = annonceRepository.findAllByIdWithVehicle(filterDTO.getAnnonceIds());

            // Vérifier si des annonces ont été trouvées
            if (annonces.isEmpty()) {
                response.setMessage("Aucune annonce trouvée");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // Appliquer les filtres
            List<Annonce> filteredAnnonces = annonces.stream()
                    .filter(annonce -> filterByPrice(annonce, filterDTO))
                    .filter(annonce -> filterByType(annonce, filterDTO))
                    .filter(annonce -> filterByKilometrage(annonce, filterDTO))
                    .collect(Collectors.toList());

            // Appliquer le tri
            if (filterDTO.getSortBy() != null) {
                sortAnnonces(filteredAnnonces, filterDTO.getSortBy());
            }

            response.setData(filteredAnnonces);
            response.setMessage("Filtrage effectué avec succès");
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            response.setMessage("Erreur lors du filtrage : " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean filterByPrice(Annonce annonce, AnnonceFilterDTO filterDTO) {
        double price = annonce.getPrice();
        if (filterDTO.getMinPrice() != null && price < filterDTO.getMinPrice()) {
            return false;
        }
        if (filterDTO.getMaxPrice() != null && price > filterDTO.getMaxPrice()) {
            return false;
        }
        return true;
    }

    private boolean filterByType(Annonce annonce, AnnonceFilterDTO filterDTO) {
        if (filterDTO.getVehicleType() == null) {
            return true;
        }
        return annonce.getVehicle().getType().equalsIgnoreCase(filterDTO.getVehicleType());
    }

    private boolean filterByKilometrage(Annonce annonce, AnnonceFilterDTO filterDTO) {
        int km = annonce.getVehicle().getKlmCounter();
        if (filterDTO.getKilometrageMin() != null && km < filterDTO.getKilometrageMin()) {
            return false;
        }
        if (filterDTO.getKilometrageMax() != null && km > filterDTO.getKilometrageMax()) {
            return false;
        }
        return true;
    }

    private void sortAnnonces(List<Annonce> annonces, String sortBy) {
        switch (sortBy.toLowerCase()) {
            case "date_asc":
                annonces.sort(Comparator.comparing(Annonce::getCreatedAt));
                break;
            case "date_desc":
                annonces.sort(Comparator.comparing(Annonce::getCreatedAt).reversed());
                break;
            case "price_asc":
                annonces.sort(Comparator.comparingDouble((Annonce a) -> a.getPrice()));
                break;
            case "price_desc":
                annonces.sort(Comparator.comparingDouble((Annonce a) -> a.getPrice()).reversed());
                break;
            default:
                throw new IllegalArgumentException("Critère de tri non valide: " + sortBy);
        }
    }
}