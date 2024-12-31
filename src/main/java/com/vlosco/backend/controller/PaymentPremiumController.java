package com.vlosco.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vlosco.backend.dto.PaymentPremiumDTO;
import com.vlosco.backend.dto.ResponseDTO;
import com.vlosco.backend.model.PaymentPremium;
import com.vlosco.backend.service.PaymentPremiumService;
import com.vlosco.backend.enums.PaymentStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/payments")
@Tag(name = "Paiements Premium", description = "API pour la gestion des paiements premium")
@CrossOrigin(origins = "*")
public class PaymentPremiumController {
    
    private final PaymentPremiumService paymentPremiumService;

    @Autowired
    public PaymentPremiumController(PaymentPremiumService paymentPremiumService) {
        this.paymentPremiumService = paymentPremiumService;
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau paiement premium",
              description = "Crée un nouveau paiement premium pour une annonce")
    public ResponseEntity<ResponseDTO<PaymentPremium>> createPayment(@RequestBody PaymentPremiumDTO paymentDTO) {
        return paymentPremiumService.createPaymentPremium(paymentDTO);
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les paiements",
              description = "Retourne la liste de tous les paiements premium")
    public ResponseEntity<ResponseDTO<List<PaymentPremium>>> getAllPayments() {
        return paymentPremiumService.getAllPayments();
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Récupérer les paiements d'un utilisateur",
              description = "Retourne la liste des paiements premium pour un utilisateur spécifique")
    public ResponseEntity<ResponseDTO<List<PaymentPremium>>> getUserPayments(@PathVariable Long userId) {
        return paymentPremiumService.getPaymentsByUserId(userId);
    }

    @PatchMapping("/{paymentId}/status")
    @Operation(summary = "Mettre à jour le statut d'un paiement",
              description = "Met à jour le statut d'un paiement premium")
    public ResponseEntity<ResponseDTO<PaymentPremium>> updatePaymentStatus(
            @PathVariable Long paymentId,
            @Parameter(description = "Nouveau statut du paiement", required = true)
            @RequestParam PaymentStatus status) {
        return paymentPremiumService.updatePaymentStatus(paymentId, status);
    }

    @DeleteMapping("/{paymentId}")
    @Operation(summary = "Supprimer un paiement",
              description = "Supprime un paiement premium spécifique")
    public ResponseEntity<ResponseDTO<Void>> deletePayment(@PathVariable Long paymentId) {
        return paymentPremiumService.deletePayment(paymentId);
    }
}
