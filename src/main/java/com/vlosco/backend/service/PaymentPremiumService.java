package com.vlosco.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vlosco.backend.repository.PaymentPremiumRepository;

@Service
public class PaymentPremiumService {
    private final PaymentPremiumRepository paymentPremiumRepository;

    @Autowired
    public PaymentPremiumService(PaymentPremiumRepository paymentPremiumRepository) {
        this.paymentPremiumRepository = paymentPremiumRepository;
    }
}
