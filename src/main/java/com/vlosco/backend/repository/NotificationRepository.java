package com.vlosco.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vlosco.backend.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
