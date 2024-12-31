package com.vlosco.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.vlosco.backend.model.Notification;
import java.util.List;
import java.time.LocalDateTime;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n WHERE n.user.userId = :userId ORDER BY n.createdAt DESC")
    List<Notification> findByUser_UserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("DELETE FROM Notification n WHERE n.user.userId = :userId")
    void deleteByUser_UserId(@Param("userId") Long userId);

    @Query("SELECT n FROM Notification n WHERE n.expirationDate < :date")
    List<Notification> findByExpirationDateLessThan(@Param("date") LocalDateTime date);
}
