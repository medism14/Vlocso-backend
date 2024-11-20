package com.vlosco.backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.vlosco.backend.dto.ResponseDTO;
import com.vlosco.backend.model.RefreshTokensBlacklist;
import com.vlosco.backend.model.User;
import com.vlosco.backend.repository.UserRepository;

/**
 * Service responsable de la gestion des JWT (JSON Web Tokens)
 * Gère la création, validation et extraction des informations des tokens
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    private SecretKey signingKey;
    private final UserRepository userRepository;

    @Autowired
    public JwtService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Initialise la clé de signature à partir de la clé secrète
     * Cette méthode est appelée après l'injection des dépendances
     */
    @PostConstruct
    public void init() {
        signingKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Génère un token d'accès avec une durée de validité d'une heure
     * @param id Identifiant de l'utilisateur
     * @return Token d'accès JWT
     */
    public String generateAccessToken(Long id) {
        return Jwts.builder()
                .setSubject(String.valueOf(id))
                .setIssuedAt(new Date())
                .claim("tokenType", "access")
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(signingKey)
                .compact();
    }

    /**
     * Génère un refresh token avec une durée de validité d'un an
     * @param id Identifiant de l'utilisateur
     * @return Refresh token JWT
     */
    public String generateRefreshToken(Long id) {
        return Jwts.builder()
                .setSubject(String.valueOf(id))
                .setIssuedAt(new Date())
                .claim("tokenType", "refresh")
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 365))
                .signWith(signingKey)
                .compact();
    }

    /**
     * Valide un token JWT et vérifie son type
     * @param token Token à valider
     * @param tokenType Type attendu du token ("access" ou "refresh")
     * @return true si le token est valide et correspond au type attendu
     */
    public boolean validateToken(String token, String tokenType) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String actualTokenType = claims.get("tokenType", String.class);
            return tokenType.equals(actualTokenType);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extrait l'identifiant utilisateur du token
     * @param token Token JWT
     * @return Identifiant de l'utilisateur
     */
    public String extractUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /**
     * Récupère les informations de l'utilisateur à partir du token
     * @param token Token JWT
     * @return ResponseEntity contenant les informations de l'utilisateur ou un message d'erreur
     */
    public ResponseEntity<ResponseDTO<User>> extractUserInfo(String token) {
        ResponseDTO<User> response = new ResponseDTO<>();
        try {
            String userId = extractUserId(token);
            Optional<User> userOptional = userRepository.findByUserIdAndIsActiveTrue(Long.parseLong(userId));
            
            if (userOptional.isPresent()) {
                response.setData(userOptional.get());
                response.setMessage("Informations de l'utilisateur récupérées avec succès");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            response.setMessage("Utilisateur non trouvé");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.setMessage("Une erreur est survenue lors de la récupération des informations de l'utilisateur");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Vérifie si un refresh token est dans la liste noire
     * @param refreshToken Token à vérifier
     * @return true si le token est blacklisté, false sinon
     */
    public Boolean isBlackList(String refreshToken) {
        String userId = extractUserId(refreshToken);
        Optional<User> user = userRepository.findById(Long.parseLong(userId));

        if (user.isPresent()) {
            User existingUser = user.get();
            List<RefreshTokensBlacklist> blacklistedRefreshToken = existingUser.getRefreshTokensBlacklist();

            // Vérifie si le token est présent dans la liste noire de l'utilisateur
            return blacklistedRefreshToken.stream()
                    .anyMatch(blacklistToken -> blacklistToken.getRefreshToken().equals(refreshToken));
        }
        return false;
    }
}
