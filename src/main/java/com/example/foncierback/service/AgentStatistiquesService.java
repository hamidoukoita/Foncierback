package com.example.foncierback.service;

import com.example.foncierback.dto.response.AgentStatistiquesResponse;
import com.example.foncierback.dto.response.PageResponse;
import com.example.foncierback.dto.response.RendezVousResponse;
import com.example.foncierback.dto.response.ReservationResponse;
import com.example.foncierback.entity.enums.PeriodeStatistiques;

/** Indicateurs de performance d'un agent, consultés par le responsable de sa société. */
public interface AgentStatistiquesService {

    /** Indicateurs seuls, calculés par la base. */
    AgentStatistiquesResponse statistiques(Long agentId, PeriodeStatistiques periode);

    /** Dossiers de réservation de l'agent sur la période, page par page (plus récents d'abord). */
    PageResponse<ReservationResponse> dossiers(Long agentId, PeriodeStatistiques periode, int page, int taille);

    /** Visites de l'agent sur la période, page par page (plus récentes d'abord). */
    PageResponse<RendezVousResponse> visites(Long agentId, PeriodeStatistiques periode, int page, int taille);
}
