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

    @Query(value = "SELECT a.* FROM annonces a JOIN vehicles v ON a.vehicle_id = v.vehicle_id WHERE v.type = :type", nativeQuery = true)
    List<Annonce> findByVehicleType(@Param("type") String type);

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

    @Query("SELECT a FROM Annonce a JOIN a.vehicle v WHERE v.type = :type AND a.annonceId != :annonceId AND a.annonceState = 'ACTIVE'")
    List<Annonce> findByVehicleTypeAndAnnonceIdNot(String type, Long annonceId);

    @Query("""
        SELECT a FROM Annonce a 
        JOIN FETCH a.vehicle v 
        JOIN FETCH a.vendor 
        WHERE v.type = :type 
        AND a.annonceId != :annonceId 
        AND a.annonceState = 'ACTIVE'
        AND (
            v.mark = :mark 
            OR v.model = :model 
            OR v.category = :category 
            OR v.fuelType = :fuelType 
            OR ABS(v.year - :year) <= 5
            OR ABS(CAST(a.price AS double) - :price) <= :price * 0.3
            OR a.city = :city
        )
        ORDER BY 
            CASE WHEN v.mark = :mark THEN 1 ELSE 0 END +
            CASE WHEN v.model = :model THEN 1 ELSE 0 END +
            CASE WHEN v.category = :category THEN 1 ELSE 0 END +
            CASE WHEN v.fuelType = :fuelType THEN 1 ELSE 0 END +
            CASE WHEN ABS(v.year - :year) <= 2 THEN 1 ELSE 0 END +
            CASE WHEN a.city = :city THEN 1 ELSE 0 END DESC
    """)
    List<Annonce> findSimilarAnnonces(
        @Param("type") String type,
        @Param("mark") String mark,
        @Param("model") String model,
        @Param("category") String category,
        @Param("fuelType") String fuelType,
        @Param("year") Integer year,
        @Param("price") String price,
        @Param("annonceId") Long annonceId,
        @Param("city") String city
    );

    @Query("SELECT DISTINCT a FROM Annonce a LEFT JOIN FETCH a.vehicle v WHERE a.annonceId IN :ids")
    List<Annonce> findAllByIdWithVehicle(@Param("ids") List<Long> ids);
}
