package com.vlosco.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vlosco.backend.dto.NotificationCreationDTO;
import com.vlosco.backend.dto.NotificationUpdateDTO;
import com.vlosco.backend.dto.ResponseDTO;
import com.vlosco.backend.model.Notification;
import com.vlosco.backend.service.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@Tag(name = "Notifications", description = "API pour la gestion des notifications")
@CrossOrigin(origins = "*")
public class NotificationController {
    
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    @Operation(summary = "Créer une nouvelle notification", 
              description = "Crée une nouvelle notification pour un utilisateur")
    public ResponseEntity<ResponseDTO<Notification>> createNotification(@RequestBody NotificationCreationDTO notificationDTO) {
        return notificationService.createNotification(
            notificationDTO.getUserId(),
            notificationDTO.getAnnonceId(),
            notificationDTO.getTitle(),
            notificationDTO.getContent(),
            notificationDTO.isGlobal(),
            notificationDTO.getExpirationDate()
        );
    }

    @GetMapping
    @Operation(summary = "Récupérer toutes les notifications", 
              description = "Retourne la liste de toutes les notifications du système")
    public ResponseEntity<ResponseDTO<List<Notification>>> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Récupérer les notifications d'un utilisateur", 
              description = "Retourne la liste des notifications pour un utilisateur spécifique")
    public ResponseEntity<ResponseDTO<List<Notification>>> getUserNotifications(@PathVariable Long userId) {
        return notificationService.getNotificationsByUserId(userId);
    }

    @PatchMapping("/{notificationId}/read")
    @Operation(summary = "Marquer une notification comme lue", 
              description = "Met à jour le statut d'une notification pour la marquer comme lue")
    public ResponseEntity<ResponseDTO<Notification>> markNotificationAsRead(@PathVariable Long notificationId) {
        return notificationService.markNotificationAsRead(notificationId);
    }

    @DeleteMapping("/{notificationId}")
    @Operation(summary = "Supprimer une notification", 
              description = "Supprime une notification spécifique")
    public ResponseEntity<ResponseDTO<Void>> deleteNotification(@PathVariable Long notificationId) {
        return notificationService.deleteNotification(notificationId);
    }

    @DeleteMapping("/user/{userId}")
    @Operation(summary = "Supprimer toutes les notifications d'un utilisateur", 
              description = "Supprime toutes les notifications associées à un utilisateur")
    public ResponseEntity<ResponseDTO<Void>> deleteAllUserNotifications(@PathVariable Long userId) {
        return notificationService.deleteAllUserNotifications(userId);
    }

    @PutMapping("/{notificationId}")
    @Operation(summary = "Mettre à jour une notification",
              description = "Met à jour les détails d'une notification existante")
    public ResponseEntity<ResponseDTO<Notification>> updateNotification(
            @PathVariable Long notificationId,
            @RequestBody NotificationUpdateDTO updateDTO) {
        return notificationService.updateNotification(notificationId, updateDTO);
    }
}
