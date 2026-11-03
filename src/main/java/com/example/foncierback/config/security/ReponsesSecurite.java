package com.example.foncierback.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * Réponses de Spring Security au même format que {@code GlobalExceptionHandler}
 * ({timestamp, status, error, message}) au lieu d'un 403 vide :
 * - 401 quand une route protégée est appelée sans session valide (jeton absent, expiré ou périmé) ;
 * - 403 quand l'utilisateur connecté n'a pas le droit d'agir.
 * Les clients se déconnectent sur un 401.
 */
@Component
public class ReponsesSecurite implements AuthenticationEntryPoint, AccessDeniedHandler {

    public static final String SESSION_EXPIREE = "Votre session a expiré : reconnectez-vous.";
    public static final String CONNEXION_REQUISE = "Connectez-vous pour effectuer cette action.";
    public static final String ACCES_REFUSE = "Accès refusé : vous n'avez pas les autorisations nécessaires.";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException exception) throws IOException {
        boolean jetonEnvoye = Boolean.TRUE.equals(request.getAttribute(JwtAuthenticationFilter.JETON_INVALIDE));
        ecrire(response, HttpStatus.UNAUTHORIZED, jetonEnvoye ? SESSION_EXPIREE : CONNEXION_REQUISE);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException exception) throws IOException {
        ecrire(response, HttpStatus.FORBIDDEN, ACCES_REFUSE);
    }

    private static void ecrire(HttpServletResponse response, HttpStatus statut, String message) throws IOException {
        response.setStatus(statut.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("{\"timestamp\":\"" + LocalDateTime.now() + "\",\"status\":" + statut.value()
                + ",\"error\":\"" + statut.getReasonPhrase() + "\",\"message\":\"" + message.replace("\"", "\\\"") + "\"}");
    }
}
