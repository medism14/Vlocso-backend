package com.vlosco.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vlosco.backend.model.PaymentPremium;

public interface PaymentPremiumRepository extends JpaRepository<PaymentPremium, Long> {

}
