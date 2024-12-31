package com.vlosco.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.vlosco.backend.model.PaymentPremium;
import java.util.List;

public interface PaymentPremiumRepository extends JpaRepository<PaymentPremium, Long> {
    @Query("SELECT p FROM PaymentPremium p WHERE p.user.userId = :userId ORDER BY p.createdAt DESC")
    List<PaymentPremium> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("SELECT p FROM PaymentPremium p WHERE p.annonce.annonceId = :annonceId ORDER BY p.createdAt DESC") 
    List<PaymentPremium> findByAnnonceIdOrderByCreatedAtDesc(@Param("annonceId") Long annonceId);
}
