package com.vlosco.backend.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service responsable du hachage et de la vérification des mots de passe
 * utilisant l'algorithme BCrypt pour une sécurité renforcée.
 */
@Service
public class PasswordService {
    /**
     * Encodeur BCrypt pour le hachage sécurisé des mots de passe
     */
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Constructeur initialisant l'encodeur BCrypt avec ses paramètres par défaut
     */
    public PasswordService() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Hache un mot de passe en utilisant BCrypt
     * @param password Le mot de passe en clair à hacher
     * @return Le mot de passe haché sous forme de chaîne
     */
    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * Vérifie si un mot de passe en clair correspond à sa version hachée
     * @param rawPassword Le mot de passe en clair à vérifier
     * @param hashedPassword Le hash BCrypt à comparer
     * @return true si les mots de passe correspondent, false sinon
     */
    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}
