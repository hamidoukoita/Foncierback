package com.example.foncierback.config.security;

import com.example.foncierback.entity.Acquereur;
import com.example.foncierback.entity.Administrateur;
import com.example.foncierback.entity.AgentPromoteur;
import com.example.foncierback.entity.Utilisateur;
import com.example.foncierback.entity.enums.StatutCompte;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Utilisateur utilisateur;
    private final String role;

    public CustomUserDetails(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        this.role = resolveRole(utilisateur);
    }

    private static String resolveRole(Utilisateur u) {
        if (u instanceof Administrateur) return "ADMIN";
        if (u instanceof AgentPromoteur) return "AGENT";
        if (u instanceof Acquereur) return "ACQUEREUR";
        return "USER";
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
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