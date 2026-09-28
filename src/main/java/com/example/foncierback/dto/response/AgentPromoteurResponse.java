package com.example.foncierback.dto.response;

import com.example.foncierback.entity.enums.StatutCompte;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentPromoteurResponse {

    private Long id;
    private String nom;
    private String prenom;
    private String telephone;
    private StatutCompte statut;
    private LocalDateTime dateCreation;
    private boolean estResponsableSociete;
    private LocalDate dateAffectation;
    private Long societeId;
    private String societeNom;
    private Long typeFonctionId;
    private String typeFonctionLibelle;
}
