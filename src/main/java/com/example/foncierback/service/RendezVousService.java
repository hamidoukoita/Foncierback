package com.example.foncierback.service;

import com.example.foncierback.dto.request.RendezVousRequest;
import com.example.foncierback.dto.response.PageResponse;
import com.example.foncierback.dto.response.RendezVousResponse;
import com.example.foncierback.entity.RendezVous;
import com.example.foncierback.entity.enums.StatutRendezVous;

import java.util.List;

public interface RendezVousService {

    RendezVousResponse create(RendezVousRequest request);

    RendezVousResponse getById(Long id);

    List<RendezVousResponse> getAll();

    RendezVousResponse update(Long id, RendezVousRequest request);

    void delete(Long id);

    RendezVousResponse changerStatut(Long id, StatutRendezVous statut, String motifRefus);

    RendezVousResponse accepter(Long id);

    RendezVousResponse refuser(Long id, String motifRefus);

    /** Annulation par l'acquéreur de son propre rendez-vous, tant qu'il n'est pas passé. */
    RendezVousResponse annulerParAcquereur(Long id);

    /** Attribue un agent et passe le RDV en ACCEPTER. */
    RendezVousResponse assignerAgent(Long id, Long agentId);

    List<RendezVousResponse> findByAcquereur(Long acquereurId);

    List<RendezVousResponse> findByAgent(Long agentId);

    /** Visites d'un agent datées depuis [debut], les plus récentes d'abord. */
    PageResponse<RendezVousResponse> pageParAgent(Long agentId, java.time.LocalDateTime debut, int page, int taille);

    List<RendezVousResponse> findBySociete(Long societeId);

    RendezVous findEntityById(Long id);
}
