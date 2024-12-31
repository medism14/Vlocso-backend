package com.vlosco.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;

import com.vlosco.backend.dto.ResponseDTO;
import com.vlosco.backend.dto.NotificationUpdateDTO;
import com.vlosco.backend.model.Annonce;
import com.vlosco.backend.model.Notification;
import com.vlosco.backend.model.User;
import com.vlosco.backend.repository.NotificationRepository;
import com.vlosco.backend.repository.UserRepository;
import com.vlosco.backend.repository.AnnonceRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final AnnonceRepository annonceRepository;

    @Autowired
    public NotificationService(
        NotificationRepository notificationRepository, 
        UserRepository userRepository,
        AnnonceRepository annonceRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.annonceRepository = annonceRepository;
    }

    @Transactional
    public ResponseEntity<ResponseDTO<Notification>> createNotification(
            Long userId, 
            Long annonceId, 
            String title, 
            String content, 
            boolean isGlobal, 
            LocalDateTime expirationDate) {
        ResponseDTO<Notification> response = new ResponseDTO<>();
        try {
            if (isGlobal) {
                List<User> allUsers = userRepository.findAll();
                List<Notification> notifications = new ArrayList<>();
                
                Optional<Annonce> annonceOpt = annonceId != null ? 
                    annonceRepository.findById(annonceId) : Optional.empty();

                for (User user : allUsers) {
                    Notification notification = new Notification();
                    notification.setUser(user);
                    annonceOpt.ifPresent(notification::setAnnonce);
                    notification.setTitle(title);
                    notification.setContent(content);
                    notification.setExpirationDate(expirationDate);
                    notification.setReadAt(null);
                    notifications.add(notification);
                }
                
                notificationRepository.saveAll(notifications);
                response.setMessage("Notifications globales créées avec succès");
                return ResponseEntity.ok(response);
            } else {
                Optional<User> userOpt = userRepository.findById(userId);
                if (!userOpt.isPresent()) {
                    response.setMessage("Utilisateur non trouvé");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }

                Optional<Annonce> annonceOpt = annonceId != null ? 
                    annonceRepository.findById(annonceId) : Optional.empty();

                Notification notification = new Notification();
                notification.setUser(userOpt.get());
                annonceOpt.ifPresent(notification::setAnnonce);
                notification.setTitle(title);
                notification.setContent(content);
                notification.setExpirationDate(expirationDate);
                notification.setReadAt(null);
                
                notification = notificationRepository.save(notification);
                
                response.setMessage("Notification créée avec succès");
                response.setData(notification);
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.setMessage("Erreur lors de la création de la notification: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<ResponseDTO<List<Notification>>> getAllNotifications() {
        ResponseDTO<List<Notification>> response = new ResponseDTO<>();
        try {
            List<Notification> notifications = notificationRepository.findAll();
            if (notifications.isEmpty()) {
                response.setMessage("Aucune notification trouvée");
                return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
            }
            response.setMessage("Notifications récupérées avec succès");
            response.setData(notifications);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setMessage("Erreur lors de la récupération des notifications: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<ResponseDTO<Notification>> getNotificationById(Long id) {
        ResponseDTO<Notification> response = new ResponseDTO<>();
        try {
            Optional<Notification> notificationOpt = notificationRepository.findById(id);
            if (!notificationOpt.isPresent()) {
                response.setMessage("Notification non trouvée");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            
            Notification notification = notificationOpt.get();
            response.setMessage("Notification récupérée avec succès");
            response.setData(notification);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setMessage("Erreur lors de la récupération de la notification: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<ResponseDTO<List<Notification>>> getNotificationsByUserId(Long userId) {
        ResponseDTO<List<Notification>> response = new ResponseDTO<>();
        try {
            List<Notification> notifications = notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(userId);
            if (notifications.isEmpty()) {
                response.setMessage("Aucune notification trouvée pour cet utilisateur");
                return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
            }
            response.setMessage("Notifications de l'utilisateur récupérées avec succès");
            response.setData(notifications);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setMessage("Erreur lors de la récupération des notifications: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<ResponseDTO<Notification>> markNotificationAsRead(Long id) {
        ResponseDTO<Notification> response = new ResponseDTO<>();
        try {
            Optional<Notification> notificationOpt = notificationRepository.findById(id);
            if (!notificationOpt.isPresent()) {
                response.setMessage("Notification non trouvée");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            
            Notification notification = notificationOpt.get();
            notification.setReadAt(LocalDateTime.now());
            notification = notificationRepository.save(notification);
            
            response.setMessage("Notification marquée comme lue");
            response.setData(notification);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setMessage("Erreur lors de la mise à jour de la notification: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<ResponseDTO<Void>> deleteNotification(Long id) {
        ResponseDTO<Void> response = new ResponseDTO<>();
        try {
            if (!notificationRepository.existsById(id)) {
                response.setMessage("Notification non trouvée");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            
            notificationRepository.deleteById(id);
            response.setMessage("Notification supprimée avec succès");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setMessage("Erreur lors de la suppression de la notification: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<ResponseDTO<Void>> deleteAllUserNotifications(Long userId) {
        ResponseDTO<Void> response = new ResponseDTO<>();
        try {
            notificationRepository.deleteByUser_UserId(userId);
            response.setMessage("Toutes les notifications de l'utilisateur ont été supprimées");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setMessage("Erreur lors de la suppression des notifications: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<ResponseDTO<Notification>> updateNotification(
            Long notificationId, 
            NotificationUpdateDTO updateDTO) {
        ResponseDTO<Notification> response = new ResponseDTO<>();
        try {
            Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
            if (!notificationOpt.isPresent()) {
                response.setMessage("Notification non trouvée");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Notification notification = notificationOpt.get();

            // Mise à jour des champs si fournis dans le DTO
            if (updateDTO.getTitle() != null) {
                notification.setTitle(updateDTO.getTitle());
            }
            if (updateDTO.getContent() != null) {
                notification.setContent(updateDTO.getContent());
            }
            if (updateDTO.getExpirationDate() != null) {
                notification.setExpirationDate(updateDTO.getExpirationDate());
            }

            // Si la notification est globale, créer une copie pour tous les utilisateurs
            if (updateDTO.isGlobal()) {
                List<User> allUsers = userRepository.findAll();
                List<Notification> notifications = new ArrayList<>();

                // Créer une copie de la notification pour chaque utilisateur
                for (User user : allUsers) {
                    Notification newNotification = new Notification();
                    newNotification.setUser(user);
                    newNotification.setAnnonce(notification.getAnnonce());
                    newNotification.setTitle(notification.getTitle());
                    newNotification.setContent(notification.getContent());
                    newNotification.setExpirationDate(notification.getExpirationDate());
                    newNotification.setReadAt(null);
                    notifications.add(newNotification);
                }

                if (!notifications.isEmpty()) {
                    notificationRepository.saveAll(notifications);
                }
            }

            notification = notificationRepository.save(notification);

            response.setMessage("Notification mise à jour avec succès");
            response.setData(notification);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage("Erreur lors de la mise à jour de la notification: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Tâche programmée qui s'exécute tous les jours à minuit
     * pour supprimer les notifications expirées
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void deleteExpiredNotifications() {
        try {
            LocalDateTime now = LocalDateTime.now();
            List<Notification> expiredNotifications = notificationRepository
                .findByExpirationDateLessThan(now);
            
            if (!expiredNotifications.isEmpty()) {
                notificationRepository.deleteAll(expiredNotifications);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
