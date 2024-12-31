package com.vlosco.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vlosco.backend.dto.PaymentPremiumDTO;
import com.vlosco.backend.dto.ResponseDTO;
import com.vlosco.backend.model.Annonce;
import com.vlosco.backend.model.PaymentPremium;
import com.vlosco.backend.model.User;
import com.vlosco.backend.repository.AnnonceRepository;
import com.vlosco.backend.repository.PaymentPremiumRepository;
import com.vlosco.backend.repository.UserRepository;
import com.vlosco.backend.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentPremiumService {
    private final PaymentPremiumRepository paymentPremiumRepository;
    private final UserRepository userRepository;
    private final AnnonceRepository annonceRepository;
    private final NotificationService notificationService;

    @Autowired
    public PaymentPremiumService(
            PaymentPremiumRepository paymentPremiumRepository,
            UserRepository userRepository,
            AnnonceRepository annonceRepository,
            NotificationService notificationService) {
        this.paymentPremiumRepository = paymentPremiumRepository;
        this.userRepository = userRepository;
        this.annonceRepository = annonceRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public ResponseEntity<ResponseDTO<PaymentPremium>> createPaymentPremium(PaymentPremiumDTO paymentDTO) {
        ResponseDTO<PaymentPremium> response = new ResponseDTO<>();
        try {
            Optional<User> user = userRepository.findById(paymentDTO.getUserId());
            Optional<Annonce> annonce = annonceRepository.findById(paymentDTO.getAnnonceId());

            if (!user.isPresent()) {
                response.setMessage("Utilisateur non trouvé");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            if (!annonce.isPresent()) {
                response.setMessage("Annonce non trouvée");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            PaymentPremium payment = new PaymentPremium();
            payment.setUser(user.get());
            payment.setAnnonce(annonce.get());
            payment.setAmount(paymentDTO.getAmount());
            payment.setPaymentMethod(paymentDTO.getPaymentMethod());
            payment.setStatus(PaymentStatus.PENDING);
            payment.setCreatedAt(LocalDateTime.now());

            payment = paymentPremiumRepository.save(payment);

            // Mettre à jour l'annonce en premium
            Annonce annonceToUpdate = annonce.get();
            annonceToUpdate.setPremium(true);
            annonceRepository.save(annonceToUpdate);

            // Notification de paiement
            notificationService.createNotification(
                user.get().getUserId(),
                annonce.get().getAnnonceId(),
                "Nouveau paiement Premium",
                "Votre paiement premium pour l'annonce est en cours de traitement",
                false,
                null
            );

            response.setMessage("Paiement premium créé avec succès");
            response.setData(payment);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage("Erreur lors de la création du paiement: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<ResponseDTO<List<PaymentPremium>>> getAllPayments() {
        ResponseDTO<List<PaymentPremium>> response = new ResponseDTO<>();
        try {
            List<PaymentPremium> payments = paymentPremiumRepository.findAll();
            if (payments.isEmpty()) {
                response.setMessage("Aucun paiement trouvé");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
            }
            response.setMessage("Paiements récupérés avec succès");
            response.setData(payments);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage("Erreur lors de la récupération des paiements: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<ResponseDTO<PaymentPremium>> getPaymentById(Long id) {
        ResponseDTO<PaymentPremium> response = new ResponseDTO<>();
        try {
            Optional<PaymentPremium> paymentOpt = paymentPremiumRepository.findById(id);
            if (!paymentOpt.isPresent()) {
                response.setMessage("Paiement non trouvé");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            PaymentPremium payment = paymentOpt.get();
            response.setMessage("Paiement récupéré avec succès");
            response.setData(payment);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage("Erreur lors de la récupération du paiement: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<ResponseDTO<List<PaymentPremium>>> getPaymentsByUserId(Long userId) {
        ResponseDTO<List<PaymentPremium>> response = new ResponseDTO<>();
        try {
            List<PaymentPremium> payments = paymentPremiumRepository.findByUserIdOrderByCreatedAtDesc(userId);
            if (payments.isEmpty()) {
                response.setMessage("Aucun paiement trouvé pour cet utilisateur");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
            }
            response.setMessage("Paiements de l'utilisateur récupérés avec succès");
            response.setData(payments);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage("Erreur lors de la récupération des paiements: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<ResponseDTO<List<PaymentPremium>>> getPaymentsByAnnonceId(Long annonceId) {
        ResponseDTO<List<PaymentPremium>> response = new ResponseDTO<>();
        try {
            List<PaymentPremium> payments = paymentPremiumRepository.findByAnnonceIdOrderByCreatedAtDesc(annonceId);
            if (payments.isEmpty()) {
                response.setMessage("Aucun paiement trouvé pour cette annonce");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
            }
            response.setMessage("Paiements de l'annonce récupérés avec succès");
            response.setData(payments);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage("Erreur lors de la récupération des paiements: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional
    public ResponseEntity<ResponseDTO<PaymentPremium>> updatePaymentStatus(Long paymentId, PaymentStatus status) {
        ResponseDTO<PaymentPremium> response = new ResponseDTO<>();
        try {
            Optional<PaymentPremium> paymentOpt = paymentPremiumRepository.findById(paymentId);
            if (!paymentOpt.isPresent()) {
                response.setMessage("Paiement non trouvé");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            PaymentPremium payment = paymentOpt.get();
            payment.setStatus(status);
            payment.setUpdatedAt(LocalDateTime.now());
            payment = paymentPremiumRepository.save(payment);

            // Si le paiement est confirmé, mettre à jour l'annonce en premium
            if (PaymentStatus.CONFIRMED.equals(status)) {
                Annonce annonce = payment.getAnnonce();
                annonce.setPremium(true);
                annonceRepository.save(annonce);

                notificationService.createNotification(
                    payment.getUser().getUserId(),
                    annonce.getAnnonceId(),
                    "Paiement Premium confirmé",
                    "Votre annonce est maintenant en mode premium",
                    false,
                    null
                );
            } else if (PaymentStatus.REJECTED.equals(status)) {
                notificationService.createNotification(
                    payment.getUser().getUserId(),
                    payment.getAnnonce().getAnnonceId(),
                    "Paiement Premium refusé",
                    "Votre paiement premium a été refusé",
                    false,
                    null
                );
            }

            response.setMessage("Statut du paiement mis à jour avec succès");
            response.setData(payment);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage("Erreur lors de la mise à jour du statut: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional
    public ResponseEntity<ResponseDTO<Void>> deletePayment(Long paymentId) {
        ResponseDTO<Void> response = new ResponseDTO<>();
        try {
            if (!paymentPremiumRepository.existsById(paymentId)) {
                response.setMessage("Paiement non trouvé");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            paymentPremiumRepository.deleteById(paymentId);
            response.setMessage("Paiement supprimé avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage("Erreur lors de la suppression du paiement: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
