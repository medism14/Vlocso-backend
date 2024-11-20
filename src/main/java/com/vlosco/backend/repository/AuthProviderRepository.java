package com.vlosco.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vlosco.backend.model.AuthProvider;
import com.vlosco.backend.model.User;

public interface AuthProviderRepository extends JpaRepository<AuthProvider, Long> {

    // Récupérer tous les utilisateurs avec leurs fournisseurs
    @Query("SELECT u FROM User u JOIN AuthProvider ap ON u = ap.user")
    Optional<List<User>> findAllUsersWithProviders();

    // Récupérer un utilisateur avec un fournisseur spécifique
    @Query("SELECT u FROM User u JOIN AuthProvider ap ON u = ap.user WHERE u.email = :email AND u.isActive = true")
    Optional<User> findUserWithEmailAndIsActive(@Param("email") String email);
}