package com.vlosco.backend.repository;

import java.util.Optional;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vlosco.backend.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.isActive = true")
    public Optional<User> findByEmailAndIsActiveTrue(@Param("email") String email);

    public Optional<User> findByEmail(@Param("email") String email);

    public Optional<User> findByEmailVerificationToken(@Param("emailVerificationToken") String emailVerificationToken);

    public Optional<User> findByPasswordVerificationToken(@Param("passwordVerificationToken") String passwordVerificationToken);

    public List<User> findByIsActiveTrue();
    
    public List<User> findByIsActiveFalse();
    
    public Long countByIsActiveTrue();
    
    public Long countByIsActiveFalse();
    
    public List<User> findByIsActiveTrueOrderByCreatedAtDesc();
    
    public List<User> findByIsActiveFalseOrderByCreatedAtDesc();
    
    public Optional<User> findByUserIdAndIsActiveTrue(@Param("userId") Long userId);
    
    public Optional<User> findByUserIdAndIsActiveFalse(@Param("userId") Long userId);
    
    public List<User> findByEmailContainingAndIsActiveTrue(@Param("emailPattern") String emailPattern);
    
}
