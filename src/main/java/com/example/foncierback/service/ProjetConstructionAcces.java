package com.example.foncierback.service;

import com.example.foncierback.config.security.SecurityUtils;
import com.example.foncierback.entity.ProjetConstruction;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Droits sur un projet de construction :
 * - l'acquéreur ne voit que ses propres dossiers ;
 * - la société destinataire (ses agents) consulte et traite les siens ;
 * - l'administration a tous les droits.
 */
@Component
public class ProjetConstructionAcces {

    public boolean estProprietaire(ProjetConstruction projet) {
        return projet.getAcquereur() != null
                && Objects.equals(projet.getAcquereur().getId(), SecurityUtils.getCurrentUserId());
    }

    public boolean estSocieteDestinataire(ProjetConstruction projet) {
        return SecurityUtils.isAgent()
                && projet.getSocietePromotrice() != null
                && Objects.equals(projet.getSocietePromotrice().getId(), SecurityUtils.getCurrentSocieteId());
    }

    public void verifierLecture(ProjetConstruction projet) {
        if (!SecurityUtils.isAdmin() && !estProprietaire(projet) && !estSocieteDestinataire(projet)) {
            throw new AccessDeniedException("Vous n'avez pas accès à ce projet de construction.");
        }
    }

    public void verifierGestion(ProjetConstruction projet) {
        if (!SecurityUtils.isAdmin() && !estSocieteDestinataire(projet)) {
            throw new AccessDeniedException("Seule la société destinataire peut traiter ce projet.");
        }
    }

    public void verifierProprietaire(ProjetConstruction projet) {
        if (!SecurityUtils.isAdmin() && !estProprietaire(projet)) {
            throw new AccessDeniedException("Seul l'acquéreur à l'origine du projet peut le compléter.");
        }
    }
}
