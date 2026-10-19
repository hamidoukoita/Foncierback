package com.example.foncierback.dto.response;

import com.example.foncierback.entity.enums.PeriodeStatistiques;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Indicateurs d'activité d'un agent sur une période (l'historique est servi à part, paginé). */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentStatistiquesResponse {

    private PeriodeStatistiques periode;
    private AgentPromoteurResponse agent;

    private Reservations reservations;
    private Visites visites;
    private Activite activite;
    private Reactivite reactivite;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Reservations {
        private long attribuees;
        private long enAttente;
        private long enCours;
        private long vendues;
        private long expirees;
        private long annulees;
        /** Part des dossiers attribués conclus par une vente, en % (null sans dossier). */
        private Integer tauxConversion;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Visites {
        private long attribuees;
        private long enAttente;
        private long prevues;
        private long effectuees;
        private long refusees;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Activite {
        /** Somme des prix des biens vendus par l'agent. */
        private BigDecimal chiffreAffaires;
        /** Montants de réservation conservés par la société (hors remboursements). */
        private BigDecimal reservationsEncaissees;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Reactivite {
        /** Délai moyen entre l'attribution du dossier et le démarrage du décompte, en heures. */
        private Double delaiMoyenPriseEnChargeHeures;
        /** Délai moyen entre le démarrage du décompte et la vente, en heures. */
        private Double delaiMoyenVenteHeures;
    }
}
