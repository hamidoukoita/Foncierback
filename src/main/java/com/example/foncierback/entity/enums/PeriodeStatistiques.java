package com.example.foncierback.entity.enums;

/** Fenêtre d'analyse des indicateurs d'un agent. */
public enum PeriodeStatistiques {
    /** 30 derniers jours. */
    MOIS(30),
    /** 90 derniers jours. */
    TRIMESTRE(90),
    /** Depuis la création du compte. */
    TOUT(0);

    private final int jours;

    PeriodeStatistiques(int jours) {
        this.jours = jours;
    }

    public int getJours() {
        return jours;
    }
}
