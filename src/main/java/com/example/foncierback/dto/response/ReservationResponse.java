package com.example.foncierback.dto.response;

import com.example.foncierback.entity.enums.StatutReservation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponse {

    private Long id;
    private String numeroDossier;
    private LocalDateTime dateReservation;
    private LocalDateTime dateExpiration;
    private StatutReservation statut;
    private String motifRefus;
    private String motifAnnulation;
    private LocalDateTime dateTraitement;
    private Long bienId;
    private Long acquereurId;
    private Long agentId;

    // Contexte d'affichage issu des relations métier existantes.
    private String bienReference;
    private String bienDesignation;
    private String programmeNom;
    private java.math.BigDecimal montant;
    private String acquereurNom;
    private String acquereurTelephone;
    private String agentNom;
    /** LOT_PROGRAMME, PARCELLE_INDIVIDUELLE ou MAISON_CLE_EN_MAIN. */
    private String typeBien;

    /** Décompte démarré par l'agent ; l'échéance est dateExpiration. */
    private LocalDateTime dateDebutDecompte;
    private LocalDateTime dateConclusion;
    /** Durée de réservation de la société, en heures. */
    private Integer dureeReservationHeures;

    // --- Paiement simulé ---
    private java.math.BigDecimal montantReservation;
    private String moyenPaiement;
    private String referencePaiement;
    private LocalDateTime datePaiement;
    private Boolean rembourse;
    private LocalDateTime dateRemboursement;
}
