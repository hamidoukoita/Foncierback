package com.example.foncierback.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        log.warn("ResourceNotFoundException: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /** Route inexistante (ou retirée) : 404 plutôt qu'une erreur interne. */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleRouteInconnue(NoResourceFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Cette adresse n'existe pas sur le serveur Foncier+.");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleMaxUpload(MaxUploadSizeExceededException ex) {
        log.warn("MaxUploadSizeExceededException: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "Le fichier dépasse la taille maximale autorisée (25 Mo)");
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException ex) {
        log.warn("BadRequestException: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /** Filet de sécurité : une donnée déjà utilisée (contrainte d'unicité) ne remonte jamais en erreur SQL brute. */
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(org.springframework.dao.DataIntegrityViolationException ex) {
        String detail = String.valueOf(ex.getMostSpecificCause().getMessage());
        log.warn("DataIntegrityViolationException: {}", detail);
        String message = detail.contains("Duplicate entry")
                ? "Une des informations saisies (e-mail, téléphone, NIF, agrément ou nom) est déjà utilisée par un autre compte."
                : "Les données envoyées ne respectent pas les contraintes de la base.";
        return buildResponse(HttpStatus.CONFLICT, message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("MethodArgumentNotValidException: {}", errors);
        return buildResponse(HttpStatus.BAD_REQUEST, errors);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("MethodArgumentTypeMismatchException: paramètre '{}' = '{}'", ex.getName(), ex.getValue());
        return buildResponse(HttpStatus.BAD_REQUEST,
            "La valeur '" + ex.getValue() + "' n'est pas valide pour le paramètre '" + ex.getName() + "'.");
}

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex,
                                                                  jakarta.servlet.http.HttpServletRequest request) {
        log.warn("AccessDeniedException: {}", ex.getMessage());
        // Refus d'un @PreAuthorize sans personne de connecté : c'est une session à rouvrir, pas un droit manquant.
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken) {
            boolean jetonRefuse = Boolean.TRUE.equals(
                    request.getAttribute(com.example.foncierback.config.security.JwtAuthenticationFilter.JETON_INVALIDE));
            return buildResponse(HttpStatus.UNAUTHORIZED, jetonRefuse
                    ? com.example.foncierback.config.security.ReponsesSecurite.SESSION_EXPIREE
                    : com.example.foncierback.config.security.ReponsesSecurite.CONNEXION_REQUISE);
        }
        return buildResponse(HttpStatus.FORBIDDEN, com.example.foncierback.config.security.ReponsesSecurite.ACCES_REFUSE);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuth(AuthenticationException ex) {
        log.warn("AuthenticationException: {}", ex.getMessage());
        // Message neutre : le détail technique (« Bad credentials »…) reste dans les logs.
        return buildResponse(HttpStatus.UNAUTHORIZED, "Votre session a expiré ou vos identifiants sont invalides. Veuillez vous reconnecter.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        log.error("Exception non gérée capturée : ", ex);
        // Jamais de message technique côté client : la trace complète est dans les logs du serveur.
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Une erreur interne est survenue. Veuillez réessayer dans un instant.");
    }

    private ResponseEntity<Map<String, Object>> buildResponse(
            HttpStatus status,
            String message
    ) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("message", message);

        return ResponseEntity
                .status(status)
                .body(response);
    }
}
