package com.vlosco.backend.repository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vlosco.backend.dto.AnnonceDetailsDTO;
import com.vlosco.backend.model.Annonce;
import com.vlosco.backend.enums.AnnonceState;

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

        @Query(value = "SELECT * FROM annonces a JOIN vehicles v ON a.vehicle_id = v.vehicle_id WHERE v.type = :type", nativeQuery = true)
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
                        @Param("minKilometrage") Integer minKilometrage,
                        @Param("maxKilometrage") Integer maxKilometrage,
                        @Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice,
                        @Param("marque") String marque);

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
                        @Param("city") String city);

        @Query("SELECT DISTINCT a FROM Annonce a LEFT JOIN FETCH a.vehicle v WHERE a.annonceId IN :ids")
        List<Annonce> findAllByIdWithVehicle(@Param("ids") List<Long> ids);

        @Query("""
                        SELECT DISTINCT a FROM Annonce a
                        JOIN FETCH a.vehicle v
                        WHERE (:type IS NULL OR v.type = :type)
                        AND (:mark IS NULL OR v.mark = :mark)
                        AND (:model IS NULL OR v.model = :model)
                        AND (:category IS NULL OR v.category = :category)
                        AND (:fuelType IS NULL OR v.fuelType = :fuelType)
                        AND (:color IS NULL OR v.color = :color)
                        AND (:city IS NULL OR a.city = :city)
                        AND (:year IS NULL OR v.year = :year)
                        AND (:transaction IS NULL OR a.transaction = :transaction)
                        AND (:minKilometrage IS NULL OR CAST(v.klmCounter AS integer) >= :minKilometrage)
                        AND (:maxKilometrage IS NULL OR CAST(v.klmCounter AS integer) <= :maxKilometrage)
                        AND (:minPrice IS NULL OR CAST(a.price AS double) >= :minPrice)
                        AND (:maxPrice IS NULL OR CAST(a.price AS double) <= :maxPrice)
                        AND a.annonceState = 'ACTIVE'
                        AND (COALESCE(:excludedIds, NULL) IS NULL OR a.annonceId NOT IN (:excludedIds))
                        ORDER BY a.createdAt ASC
                        LIMIT :limit
                        """)
        List<Annonce> searchAnnoncesAdvanced(
                        @Param("type") String type,
                        @Param("transaction") String transaction,
                        @Param("mark") String mark,
                        @Param("model") String model,
                        @Param("category") String category,
                        @Param("fuelType") String fuelType,
                        @Param("color") String color,
                        @Param("city") String city,
                        @Param("year") Integer year,
                        @Param("minKilometrage") Integer minKilometrage,
                        @Param("maxKilometrage") Integer maxKilometrage,
                        @Param("minPrice") Double minPrice,
                        @Param("maxPrice") Double maxPrice,
                        @Param("excludedIds") String[] excludedIds,
                        @Param("limit") Integer limit);

        @Query("""
                        SELECT DISTINCT a FROM Annonce a
                        JOIN FETCH a.vehicle v
                        WHERE (:type IS NULL OR v.type = :type)
                        AND (:mark IS NULL OR v.mark = :mark)
                        AND (:model IS NULL OR v.model = :model)
                        AND (:category IS NULL OR v.category = :category)
                        AND (:fuelType IS NULL OR v.fuelType = :fuelType)
                        AND (:color IS NULL OR v.color = :color)
                        AND (:city IS NULL OR a.city = :city)
                        AND (:year IS NULL OR v.year = :year)
                        AND (:transaction IS NULL OR a.transaction = :transaction)
                        AND (:minKilometrage IS NULL OR CAST(v.klmCounter AS integer) >= :minKilometrage)
                        AND (:maxKilometrage IS NULL OR CAST(v.klmCounter AS integer) <= :maxKilometrage)
                        AND (:minPrice IS NULL OR CAST(a.price AS double) >= :minPrice)
                        AND (:maxPrice IS NULL OR CAST(a.price AS double) <= :maxPrice)
                        AND a.annonceState = 'ACTIVE'
                        AND (COALESCE(:excludedIds, NULL) IS NULL OR a.annonceId NOT IN (:excludedIds))
                        ORDER BY a.createdAt ASC
                        LIMIT :limit
                        """)
        List<Annonce> searchAnnoncesAdvancedDateAsc(
                        @Param("type") String type,
                        @Param("transaction") String transaction,
                        @Param("mark") String mark,
                        @Param("model") String model,
                        @Param("category") String category,
                        @Param("fuelType") String fuelType,
                        @Param("color") String color,
                        @Param("city") String city,
                        @Param("year") Integer year,
                        @Param("minKilometrage") Integer minKilometrage,
                        @Param("maxKilometrage") Integer maxKilometrage,
                        @Param("minPrice") Double minPrice,
                        @Param("maxPrice") Double maxPrice,
                        @Param("excludedIds") String[] excludedIds,
                        @Param("limit") Integer limit);

        @Query("""
                        SELECT DISTINCT a FROM Annonce a
                        JOIN FETCH a.vehicle v
                        WHERE (:type IS NULL OR v.type = :type)
                        AND (:mark IS NULL OR v.mark = :mark)
                        AND (:model IS NULL OR v.model = :model)
                        AND (:category IS NULL OR v.category = :category)
                        AND (:fuelType IS NULL OR v.fuelType = :fuelType)
                        AND (:color IS NULL OR v.color = :color)
                        AND (:city IS NULL OR a.city = :city)
                        AND (:year IS NULL OR v.year = :year)
                        AND (:transaction IS NULL OR a.transaction = :transaction)
                        AND (:minKilometrage IS NULL OR CAST(v.klmCounter AS integer) >= :minKilometrage)
                        AND (:maxKilometrage IS NULL OR CAST(v.klmCounter AS integer) <= :maxKilometrage)
                        AND (:minPrice IS NULL OR CAST(a.price AS double) >= :minPrice)
                        AND (:maxPrice IS NULL OR CAST(a.price AS double) <= :maxPrice)
                        AND a.annonceState = 'ACTIVE'
                        AND (COALESCE(:excludedIds, NULL) IS NULL OR a.annonceId NOT IN (:excludedIds))
                        ORDER BY a.createdAt DESC
                        LIMIT :limit
                        """)
        List<Annonce> searchAnnoncesAdvancedDateDesc(
                        @Param("type") String type,
                        @Param("transaction") String transaction,
                        @Param("mark") String mark,
                        @Param("model") String model,
                        @Param("category") String category,
                        @Param("fuelType") String fuelType,
                        @Param("color") String color,
                        @Param("city") String city,
                        @Param("year") Integer year,
                        @Param("minKilometrage") Integer minKilometrage,
                        @Param("maxKilometrage") Integer maxKilometrage,
                        @Param("minPrice") Double minPrice,
                        @Param("maxPrice") Double maxPrice,
                        @Param("excludedIds") String[] excludedIds,
                        @Param("limit") Integer limit);

        @Query("""
                        SELECT DISTINCT a FROM Annonce a
                        JOIN FETCH a.vehicle v
                        WHERE (:type IS NULL OR v.type = :type)
                        AND (:mark IS NULL OR v.mark = :mark)
                        AND (:model IS NULL OR v.model = :model)
                        AND (:category IS NULL OR v.category = :category)
                        AND (:fuelType IS NULL OR v.fuelType = :fuelType)
                        AND (:color IS NULL OR v.color = :color)
                        AND (:city IS NULL OR a.city = :city)
                        AND (:year IS NULL OR v.year = :year)
                        AND (:transaction IS NULL OR a.transaction = :transaction)
                        AND (:minKilometrage IS NULL OR CAST(v.klmCounter AS integer) >= :minKilometrage)
                        AND (:maxKilometrage IS NULL OR CAST(v.klmCounter AS integer) <= :maxKilometrage)
                        AND (:minPrice IS NULL OR CAST(a.price AS double) >= :minPrice)
                        AND (:maxPrice IS NULL OR CAST(a.price AS double) <= :maxPrice)
                        AND a.annonceState = 'ACTIVE'
                        AND (COALESCE(:excludedIds, NULL) IS NULL OR a.annonceId NOT IN (:excludedIds))
                        ORDER BY a.price ASC
                        LIMIT :limit
                        """)
        List<Annonce> searchAnnoncesAdvancedPriceAsc(
                        @Param("type") String type,
                        @Param("transaction") String transaction,
                        @Param("mark") String mark,
                        @Param("model") String model,
                        @Param("category") String category,
                        @Param("fuelType") String fuelType,
                        @Param("color") String color,
                        @Param("city") String city,
                        @Param("year") Integer year,
                        @Param("minKilometrage") Integer minKilometrage,
                        @Param("maxKilometrage") Integer maxKilometrage,
                        @Param("minPrice") Double minPrice,
                        @Param("maxPrice") Double maxPrice,
                        @Param("excludedIds") String[] excludedIds,
                        @Param("limit") Integer limit);

        @Query("""
                        SELECT DISTINCT a FROM Annonce a
                        JOIN FETCH a.vehicle v
                        WHERE (:type IS NULL OR v.type = :type)
                        AND (:mark IS NULL OR v.mark = :mark)
                        AND (:model IS NULL OR v.model = :model)
                        AND (:category IS NULL OR v.category = :category)
                        AND (:fuelType IS NULL OR v.fuelType = :fuelType)
                        AND (:color IS NULL OR v.color = :color)
                        AND (:city IS NULL OR a.city = :city)
                        AND (:year IS NULL OR v.year = :year)
                        AND (:transaction IS NULL OR a.transaction = :transaction)
                        AND (:minKilometrage IS NULL OR CAST(v.klmCounter AS integer) >= :minKilometrage)
                        AND (:maxKilometrage IS NULL OR CAST(v.klmCounter AS integer) <= :maxKilometrage)
                        AND (:minPrice IS NULL OR CAST(a.price AS double) >= :minPrice)
                        AND (:maxPrice IS NULL OR CAST(a.price AS double) <= :maxPrice)
                        AND a.annonceState = 'ACTIVE'
                        AND (COALESCE(:excludedIds, NULL) IS NULL OR a.annonceId NOT IN (:excludedIds))
                        ORDER BY a.price DESC
                        LIMIT :limit
                        """)
        List<Annonce> searchAnnoncesAdvancedPriceDesc(
                        @Param("type") String type,
                        @Param("transaction") String transaction,
                        @Param("mark") String mark,
                        @Param("model") String model,
                        @Param("category") String category,
                        @Param("fuelType") String fuelType,
                        @Param("color") String color,
                        @Param("city") String city,
                        @Param("year") Integer year,
                        @Param("minKilometrage") Integer minKilometrage,
                        @Param("maxKilometrage") Integer maxKilometrage,
                        @Param("minPrice") Double minPrice,
                        @Param("maxPrice") Double maxPrice,
                        @Param("excludedIds") String[] excludedIds,
                        @Param("limit") Integer limit);

        @Query("""
                        SELECT a FROM Annonce a
                        JOIN FETCH a.vehicle v
                        JOIN FETCH a.vendor
                        LEFT JOIN a.interactions i
                        WHERE a.annonceState = 'ACTIVE'
                        AND (COALESCE(:excludedIds, NULL) IS NULL OR a.annonceId NOT IN (:excludedIds))
                        GROUP BY a.annonceId, v, a.vendor
                        ORDER BY COUNT(i) DESC, a.premium DESC
                        LIMIT :limit
                        """)
        List<Annonce> findMostPopularAnnonces(@Param("limit") Integer limit,
                        @Param("excludedIds") String[] excludedIds);

        @Query(value = """
                        SELECT a.*
                        FROM annonces a
                        LEFT JOIN interactions i ON a.annonce_id = i.annonce_id
                        LEFT JOIN vehicles v ON a.vehicle_id = v.vehicle_id
                        WHERE a.annonce_state = 'ACTIVE'
                        AND (:type IS NULL OR v.type = :type)
                        GROUP BY a.annonce_id, v.vehicle_id, a.premium, a.created_at
                        ORDER BY COUNT(i.interaction_id) DESC, a.premium DESC, a.created_at DESC
                        LIMIT :limit
                        """, nativeQuery = true)
        List<Annonce> findPopularAnnonces(
                        @Param("type") String type,
                        @Param("limit") Integer limit);

        @Query(value = """
                        SELECT a.*
                        FROM annonces a
                        LEFT JOIN interactions i ON a.annonce_id = i.annonce_id
                        LEFT JOIN vehicles v ON a.vehicle_id = v.vehicle_id
                        WHERE a.annonce_state = 'ACTIVE'
                        AND (:type IS NULL OR v.type = :type)
                        AND (:excludedIds IS NULL OR a.annonce_id NOT IN (:excludedIds))
                        GROUP BY a.annonce_id, v.vehicle_id, a.premium, a.created_at
                        ORDER BY COUNT(i.interaction_id) DESC, a.premium DESC, a.created_at DESC
                        LIMIT :limit
                        """, nativeQuery = true)
        List<Annonce> findPopularAnnonces(
                        @Param("type") String type,
                        @Param("excludedIds") List<Long> excludedIds,
                        @Param("limit") Integer limit);

        @Query("""
                        SELECT DISTINCT a FROM Annonce a
                        JOIN FETCH a.vehicle v
                        JOIN FETCH a.vendor
                        WHERE 1=1
                        AND (:searchText IS NULL OR
                            (LOWER(a.title) LIKE LOWER(CONCAT('%', :searchText, '%'))
                            OR LOWER(v.mark) LIKE LOWER(CONCAT('%', :searchText, '%'))
                            OR LOWER(v.model) LIKE LOWER(CONCAT('%', :searchText, '%'))))
                        AND (:type IS NULL OR v.type = :type)
                        AND (:transaction IS NULL OR a.transaction = :transaction)
                        AND (:mark IS NULL OR v.mark = :mark)
                        AND (:model IS NULL OR v.model = :model)
                        AND (:category IS NULL OR v.category = :category)
                        AND (:fuelType IS NULL OR v.fuelType = :fuelType)
                        AND (:color IS NULL OR v.color = :color)
                        AND (:city IS NULL OR a.city = :city)
                        AND (:year IS NULL OR v.year = :year)
                        AND (:minPrice IS NULL OR a.price >= :minPrice)
                        AND (:maxPrice IS NULL OR a.price <= :maxPrice)
                        AND (:minKilometrage IS NULL OR v.klmCounter >= :minKilometrage)
                        AND (:maxKilometrage IS NULL OR v.klmCounter <= :maxKilometrage)
                        AND a.annonceState = 'ACTIVE'
                        AND (COALESCE(:excludedIds) IS NULL OR a.annonceId NOT IN (:excludedIds))
                        ORDER BY
                            CASE
                                WHEN :sortBy = 'price_asc' THEN a.price
                                WHEN :sortBy = 'price_desc' THEN a.price
                                WHEN :sortBy = 'date_asc' THEN a.createdAt
                                WHEN :sortBy = 'date_desc' THEN a.createdAt
                                WHEN :sortBy = 'kilometrage_asc' THEN v.klmCounter
                                WHEN :sortBy = 'kilometrage_desc' THEN v.klmCounter
                            END ASC,
                            CASE
                                WHEN :sortBy LIKE '%_desc' THEN 1
                                ELSE 0
                            END DESC,
                            a.premium DESC,
                            a.createdAt DESC
                        """)
        List<Annonce> searchAnnoncesWithFilters(
                        @Param("searchText") String searchText,
                        @Param("type") String type,
                        @Param("transaction") String transaction,
                        @Param("mark") String mark,
                        @Param("model") String model,
                        @Param("category") String category,
                        @Param("fuelType") String fuelType,
                        @Param("color") String color,
                        @Param("city") String city,
                        @Param("year") Integer year,
                        @Param("minPrice") Double minPrice,
                        @Param("maxPrice") Double maxPrice,
                        @Param("minKilometrage") Integer minKilometrage,
                        @Param("maxKilometrage") Integer maxKilometrage,
                        @Param("sortBy") String sortBy,
                        @Param("excludedIds") String[] excludedIds);

        List<Annonce> findByAnnonceStateAndEndDateBefore(AnnonceState state, LocalDateTime date);

        @Query("SELECT a FROM Annonce a WHERE a.annonceState = :state AND a.endDate BETWEEN :startDate AND :endDate")
        List<Annonce> findByAnnonceStateAndEndDateBetween(
            @Param("state") AnnonceState state, 
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
        );

        @Query("""
        SELECT a FROM Annonce a 
        WHERE (:premium IS NULL OR a.premium = :premium)
        AND (:transaction IS NULL OR a.transaction = :transaction)
        AND (:city IS NULL OR a.city = :city)
        AND (:minKilometrage IS NULL OR CAST(a.vehicle.klmCounter AS integer) >= :minKilometrage)
        AND (:maxKilometrage IS NULL OR CAST(a.vehicle.klmCounter AS integer) <= :maxKilometrage)
        AND (:minPrice IS NULL OR CAST(a.price AS double) >= :minPrice)
        AND (:maxPrice IS NULL OR CAST(a.price AS double) <= :maxPrice)
        AND (:mark IS NULL OR a.vehicle.mark = :mark)
        AND (:model IS NULL OR a.vehicle.model = :model)
        AND (:type IS NULL OR a.vehicle.type = :type)
        AND a.annonceId IN :annonceIds
        """)
        List<Annonce> filterAnnonces(
            @Param("annonceIds") List<Long> annonceIds,
            @Param("premium") Boolean premium,
            @Param("transaction") String transaction,
            @Param("city") String city,
            @Param("minKilometrage") Integer minKilometrage,
            @Param("maxKilometrage") Integer maxKilometrage,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("mark") String mark,
            @Param("model") String model,
            @Param("type") String type
        );

        @Query("""
            SELECT a FROM Annonce a 
            JOIN PaymentPremium p ON p.annonce = a 
            WHERE a.premium = :premium 
            AND p.endDate < :date
            AND p.status = 'CONFIRMED'
            ORDER BY p.endDate DESC
        """)
        List<Annonce> findByPremiumAndPremiumEndDateBefore(
            @Param("premium") boolean premium, 
            @Param("date") LocalDateTime date
        );

        @Query("""
            SELECT a FROM Annonce a 
            JOIN PaymentPremium p ON p.annonce = a 
            WHERE a.premium = :premium 
            AND p.endDate BETWEEN :startDate AND :endDate
            AND p.status = 'CONFIRMED'
            ORDER BY p.endDate ASC
        """)
        List<Annonce> findByPremiumAndPremiumEndDateBetween(
            @Param("premium") boolean premium,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
        );

        @Query("""
            SELECT a FROM Annonce a 
            JOIN PaymentPremium p ON p.annonce = a 
            WHERE a.premium = true 
            AND p.endDate < :currentDate 
            AND p.status = 'CONFIRMED'
            ORDER BY p.endDate DESC
        """)
        List<Annonce> findPremiumAnnoncesWithExpiredPayment(@Param("currentDate") LocalDateTime currentDate);

        @Query("""
            SELECT a FROM Annonce a 
            JOIN PaymentPremium p ON p.annonce = a 
            WHERE a.premium = true 
            AND p.endDate BETWEEN :startDate AND :endDate 
            AND p.status = 'CONFIRMED'
            ORDER BY p.endDate ASC
        """)
        List<Annonce> findPremiumAnnoncesWithPaymentExpiringBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
        );
}
