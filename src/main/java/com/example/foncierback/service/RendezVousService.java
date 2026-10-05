package com.example.foncierback.service;

import com.example.foncierback.dto.request.RendezVousRequest;
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

    List<RendezVousResponse> findByAcquereur(Long acquereurId);

    List<RendezVousResponse> findByAgent(Long agentId);

    List<RendezVousResponse> findBySociete(Long societeId);

    RendezVous findEntityById(Long id);
}
