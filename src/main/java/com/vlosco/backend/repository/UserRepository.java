package com.vlosco.backend.repository;

import java.util.Optional;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.vlosco.backend.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    public Optional<User> findByEmailAndIsActiveTrue(String email);

    public Optional<User> findByEmail(String email);

    public Optional<User> findByEmailVerificationToken(String emailVerificationToken);

    public Optional<User> findByPasswordVerificationToken(String passwordVerificationToken);

    public List<User> findByIsActiveTrue();
    
    public List<User> findByIsActiveFalse();
    
    public Long countByIsActiveTrue();
    
    public Long countByIsActiveFalse();
    
    public List<User> findByIsActiveTrueOrderByCreatedAtDesc();
    
    public List<User> findByIsActiveFalseOrderByCreatedAtDesc();
    
    public Optional<User> findByUserIdAndIsActiveTrue(Long userId);
    
    public Optional<User> findByUserIdAndIsActiveFalse(Long userId);
    
    public List<User> findByEmailContainingAndIsActiveTrue(String emailPattern);
    
}
