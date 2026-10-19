package com.example.foncierback.entity.enums;

/**
 * Suite donnée à une réservation payée dont le bien a été vendu à un autre acquéreur :
 * l'acquéreur choisit entre le remboursement et le report de son paiement sur un autre bien de la société.
 */
public enum SuiteVente {
    /** En attente du choix de l'acquéreur (remboursé d'office passé le délai). */
    A_CHOISIR,
    /** Remboursement demandé : versé (simulé) à l'échéance prévue. */
    REMBOURSEMENT_EN_COURS,
    /** Montant rendu à l'acquéreur. */
    REMBOURSEE,
    /** Montant reporté sur la réservation d'un autre bien de la société. */
    REMPLACEE
}
