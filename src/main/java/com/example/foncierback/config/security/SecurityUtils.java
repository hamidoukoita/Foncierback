package com.example.foncierback.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component("securityUtils")
public class SecurityUtils {

    /**
     * Récupère les détails de l'utilisateur connecté actuellement.
     */
    public static CustomUserDetails getCurrentUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails;
        }
        return null;
    }

    /**
     * Récupère l'ID de l'utilisateur connecté.
     */
    public static Long getCurrentUserId() {
        CustomUserDetails user = getCurrentUserDetails();
        return user != null ? user.getUtilisateur().getId() : null;
    }

    /**
     * Récupère le rôle de l'utilisateur connecté (ADMIN, AGENT, ACQUEREUR).
     */
    public static String getCurrentUserRole() {
        CustomUserDetails user = getCurrentUserDetails();
        return user != null ? user.getRole() : null;
    }

    /**
     * Vérifie si l'utilisateur connecté est Administrateur.
     */
    public static boolean isAdmin() {
        return "ADMIN".equals(getCurrentUserRole());
    }

    /**
     * Vérifie si l'utilisateur connecté est un Agent Promoteur.
     */
    public static boolean isAgent() {
        return "AGENT".equals(getCurrentUserRole());
    }

    /**
     * Vérifie si l'utilisateur connecté est un Acquéreur.
     */
    public static boolean isAcquereur() {
        return "ACQUEREUR".equals(getCurrentUserRole());
    }

    /**
     * Récupère l'identifiant de la société promotrice de l'agent connecté.
     */
    public static Long getCurrentSocieteId() {
        CustomUserDetails user = getCurrentUserDetails();
        return user != null ? user.getSocieteId() : null;
    }

    /**
     * Vérifie si l'utilisateur est le propriétaire de la ressource (même utilisateur ID) ou ADMIN.
     */
    public static boolean isOwnerOrAdmin(Long userId) {
        if (isAdmin()) return true;
        Long currentId = getCurrentUserId();
        return currentId != null && Objects.equals(currentId, userId);
    }

    /**
     * Vérifie si l'agent appartient à la société donnée ou est ADMIN.
     */
    public static boolean belongsToSocieteOrAdmin(Long societeId) {
        if (isAdmin()) return true;
        Long currentSocieteId = getCurrentSocieteId();
        return currentSocieteId != null && Objects.equals(currentSocieteId, societeId);
    }

    /**
     * Vérifie si l'utilisateur a une permission spécifique.
     */
    public static boolean hasPermission(String permission) {
        CustomUserDetails user = getCurrentUserDetails();
        return user != null && (user.getPermissions().contains("OP_ALL") || user.getPermissions().contains(permission));
    }
}
