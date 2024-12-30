/**
 * Filtre de requêtes HTTP pour la validation des tokens JWT.
 * Intercepte chaque requête pour vérifier et valider le token d'authentification,
 * à l'exception des chemins d'authentification définis.
 */
package com.vlosco.backend.security;

import com.vlosco.backend.service.JwtService;
import org.springframework.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    // Liste des chemins exclus de la vérification JWT (ex: endpoints d'authentification)
    // private static final List<String> EXCLUDED_PATHS = Arrays.asList("/auth/");
    private static final List<String> EXCLUDED_PATHS = Arrays.asList("/", "/auth", "/users", "/annonce", "/swagger-ui", "/api-docs", "/providers", "/conversations", "/messages", "/health");

    public JwtRequestFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Méthode principale de filtrage des requêtes.
     * Vérifie la présence et la validité du token JWT dans l'en-tête Authorization.
     * Si le token est valide, configure le contexte de sécurité avec l'authentification de l'utilisateur.
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Bypass du filtre pour les chemins exclus
        for (String excluded_path : EXCLUDED_PATHS) {
            if (path.startsWith(excluded_path)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        final String authHeader = request.getHeader("Authorization");

        // Vérification de la présence et du format du header Authorization
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"message\": \"Missing or invalid Authorization header\"}");
            return;
        }

        // Extraction et validation du token JWT
        String jwt = authHeader.substring(7);

        if (jwtService.validateToken(jwt, "access")) {
            // Configuration du contexte de sécurité avec l'ID utilisateur extrait du token
            String userId = jwtService.extractUserId(jwt);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } else {
            // Réponse en cas de token invalide
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"message\": \"Unauthorized: Invalid JWT token\"}");
        }
    }
}