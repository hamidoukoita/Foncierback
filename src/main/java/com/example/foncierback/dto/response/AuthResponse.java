package com.example.foncierback.dto.response;

import com.example.foncierback.entity.enums.StatutCompte;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    @Builder.Default
    private String type = "Bearer";

    private String token;
    private Long id;
    private String nom;
    private String prenom;
    private String telephone;
    private String role;
    private StatutCompte statut;

    // Métadonnées sur les permissions et le contexte
    private List<String> permissions;
    private Long societeId;
    private String societeNom;
    private Boolean estResponsableSociete;
    private String fonctionLibelle;
    private String niveauAccesCode;
}
