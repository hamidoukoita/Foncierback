package com.example.foncierback.entity.enums;

/**
 * Cycle d'une réservation : payée et en attente d'un agent, puis décompte démarré par l'agent,
 * jusqu'à la vente ou l'expiration. L'acquéreur peut annuler tant que le bien n'est pas vendu.
 */
public enum StatutReservation {
    /** Payée, en attente du démarrage du décompte par l'agent. */
    EN_ATTENTE,
    /** Décompte démarré par l'agent : le bien est bloqué jusqu'à l'échéance. */
    EN_COURS,
    /** Vente conclue avant l'échéance. */
    VENDUE,
    /** Échéance atteinte sans vente : le montant n'est pas rendu, le bien est libéré. */
    EXPIREE,
    /** Annulée par l'acquéreur (non remboursée) ou parce que le bien a été vendu à un autre (remboursée). */
    ANNULEE,
    /** Anciens dossiers, avant la réservation payante. */
    CONFIRMER,
    /** Anciens dossiers, avant la réservation payante. */
    REFUSER
}
