package com.vlosco.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vlosco.backend.dto.AnnonceDetailsDTO;
import com.vlosco.backend.model.Annonce;

public interface AnnonceRepository extends JpaRepository<Annonce, Long> {
    // Méthodes personnalisées basées sur le modèle Annonce
    @Query(value = "SELECT * FROM annonces WHERE vendor_id = :vendorId", nativeQuery = true)
    List<Annonce> findByVendorId(@Param("vendorId") Long vendorId);

    @Query(value = "SELECT * FROM annonces WHERE LOWER(title) LIKE LOWER(CONCAT('%', :keyword, '%'))", nativeQuery = true)
    List<Annonce> findByTitleContaining(@Param("keyword") String keyword);

    List<Annonce> findByCountry(String country);

    List<Annonce> findByCity(String city);

    @Query(value = "SELECT * FROM annonces WHERE vehicle_id = :vehicleId", nativeQuery = true)
    Optional<List<Annonce>> findByVehicleId(@Param("vehicleId") Long vehicleId);

    @Query(value = "SELECT * FROM annonces WHERE premium = :premium", nativeQuery = true)
    Optional<List<Annonce>> findByPremium(@Param("premium") Boolean premium);

    @Query(value = "SELECT * FROM annonces WHERE transaction = :transaction", nativeQuery = true)
    Optional<List<Annonce>> findByTransaction(@Param("transaction") String transaction);

    @Query(value = "SELECT * FROM annonces WHERE annonce_state = :state", nativeQuery = true)
    Optional<List<Annonce>> findByAnnonceState(@Param("state") String state);

    @Query(value = "SELECT * FROM annonces WHERE id = :id AND premium = true", nativeQuery = true)
    Optional<Annonce> findPremiumAnnonceById(@Param("id") Long id);

    /**
     * Récupère les détails d'une annonce spécifique.
     * 
     * @param id Identifiant de l'annonce
     * @return Optional contenant les détails de l'annonce si trouvés
     */
    @Query(value = "SELECT * FROM annonces WHERE id = :id", nativeQuery = true)
    Optional<AnnonceDetailsDTO> findDetailsById(@Param("id") Long id);

    // Cette requête permet de filtrer les annonces en fonction de plusieurs
    // critères.
    @Query("SELECT a FROM Annonce a JOIN a.vehicle v WHERE (:vendorId IS NULL OR a.vendor.userId = :vendorId) " +
            "AND (:premium IS NULL OR a.premium = :premium) " +
            "AND (:transaction IS NULL OR a.transaction = :transaction) " +
            "AND (:annonceState IS NULL OR a.annonceState = :annonceState) " +
            "AND (:city IS NULL OR a.city = :city) " +
            "AND (:minKilometrage IS NULL OR v.klmCounter >= :minKilometrage) " +
            "AND (:maxKilometrage IS NULL OR v.klmCounter <= :maxKilometrage) " +
            "AND (:minPrice IS NULL OR a.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR a.price <= :maxPrice) " +
            "AND (:marque IS NULL OR v.mark = :marque)")
    Optional<List<Annonce>> filterAnnonces(@Param("vendorId") Long vendorId, @Param("premium") Boolean premium,
            @Param("transaction") String transaction, @Param("annonceState") String annonceState,
            @Param("city") String city,
            @Param("minKilometrage") Integer minKilometrage, @Param("maxKilometrage") Integer maxKilometrage,
            @Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice, @Param("marque") String marque);
}
