package com.example.foncierback.config.security;

import com.example.foncierback.entity.Acquereur;
import com.example.foncierback.entity.Administrateur;
import com.example.foncierback.entity.AgentPromoteur;
import com.example.foncierback.entity.NiveauAcces;
import com.example.foncierback.entity.TypeFonction;
import com.example.foncierback.entity.Utilisateur;
import com.example.foncierback.entity.enums.StatutCompte;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Utilisateur utilisateur;
    private final String role;
    private final List<String> permissions;
    private final Long societeId;
    private final String societeNom;
    private final String fonctionLibelle;
    private final String niveauAccesCode;

    public CustomUserDetails(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        this.role = resolveRole(utilisateur);
        this.permissions = resolvePermissions(utilisateur, this.role);

        if (utilisateur instanceof AgentPromoteur agent) {
            Long sId = null;
            String sNom = null;
            String fLibelle = null;
            String nCode = null;
            try {
                sId = agent.getSocietePromotrice() != null ? agent.getSocietePromotrice().getId() : null;
                sNom = agent.getSocietePromotrice() != null ? agent.getSocietePromotrice().getNom() : null;
                fLibelle = agent.getTypeFonction() != null ? agent.getTypeFonction().getLibelle() : null;
                nCode = (agent.getTypeFonction() != null && agent.getTypeFonction().getNiveauAcces() != null)
                        ? agent.getTypeFonction().getNiveauAcces().getCode()
                        : null;
            } catch (Exception ignored) {
            }
            this.societeId = sId;
            this.societeNom = sNom;
            this.fonctionLibelle = fLibelle;
            this.niveauAccesCode = nCode;
        } else {
            this.societeId = null;
            this.societeNom = null;
            this.fonctionLibelle = null;
            this.niveauAccesCode = null;
        }
    }

    private static String resolveRole(Utilisateur u) {
        if (u instanceof Administrateur) return "ADMIN";
        if (u instanceof AgentPromoteur) return "AGENT";
        if (u instanceof Acquereur) return "ACQUEREUR";
        return "USER";
    }

    private static List<String> resolvePermissions(Utilisateur u, String role) {
        List<String> perms = new ArrayList<>();

        switch (role) {
            case "ADMIN" -> {
                perms.add("OP_ALL");
                perms.add("GESTION_UTILISATEURS");
                perms.add("GESTION_SOCIETES");
                perms.add("VALIDATION_AGREMENT");
                perms.add("GESTION_REFERENTIELS");
                perms.add("GESTION_PROGRAMMES");
                perms.add("GESTION_PARCELLES");
                perms.add("GESTION_RESERVATIONS");
                perms.add("GESTION_RDV");
                perms.add("GESTION_PROJETS");
            }
            case "AGENT" -> {
                perms.add("CONSULTER_BIENS");
                perms.add("GESTION_PROGRAMMES_PROPRES");
                perms.add("GESTION_PARCELLES_PROPRES");
                perms.add("TRAITER_RESERVATIONS");
                perms.add("TRAITER_RDV");
                perms.add("TRAITER_PROJETS");
                perms.add("GESTION_CRENEAUX");

                if (u instanceof AgentPromoteur agent) {
                    if (agent.isEstResponsableSociete()) {
                        perms.add("RESPONSABLE_SOCIETE");
                        perms.add("GESTION_AGENTS_SOCIETE");
                    }
                    TypeFonction tf = agent.getTypeFonction();
                    if (tf != null) {
                        perms.add("FONCTION_" + tf.getCode());
                        NiveauAcces na = tf.getNiveauAcces();
                        if (na != null && na.getCode() != null) {
                            perms.add("NIVEAU_" + na.getCode().toUpperCase());
                        }
                    }
                }
            }
            case "ACQUEREUR" -> {
                perms.add("CONSULTER_BIENS");
                perms.add("CREER_RESERVATION");
                perms.add("DEMANDER_RDV");
                perms.add("SOUMETTRE_PROJET");
                perms.add("CONSULTER_MES_DOSSIERS");
            }
            default -> perms.add("CONSULTER_BIENS");
        }

        return Collections.unmodifiableList(perms);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        // Rôle principal avec préfixe ROLE_
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));

        // Rôle fonctionnel lié au niveau d'accès pour les agents
        if (niveauAccesCode != null && !niveauAccesCode.isBlank()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + niveauAccesCode.toUpperCase()));
        }

        // Permissions détaillées (authorities directes)
        for (String perm : permissions) {
            authorities.add(new SimpleGrantedAuthority(perm));
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return utilisateur.getMotDePasse();
    }

    @Override
    public String getUsername() {
        return utilisateur.getTelephone();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return utilisateur.getStatut() != StatutCompte.SUSPENDU
                && utilisateur.getStatut() != StatutCompte.DESACTIVE;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return utilisateur.getStatut() == StatutCompte.ACTIF;
    }
}
