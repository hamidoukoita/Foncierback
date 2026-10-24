package com.example.foncierback.entity.enums;

/**
 * EN_ATTENTE : l'acquéreur n'a pas encore répondu ;
 * CADUQUE : une autre proposition a été retenue, ou la société a tranché (acceptation / refus du projet).
 */
public enum StatutProposition {
    EN_ATTENTE,
    ACCEPTEE,
    REFUSEE,
    CADUQUE
}
