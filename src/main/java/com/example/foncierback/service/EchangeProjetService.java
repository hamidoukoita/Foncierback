package com.example.foncierback.service;

import com.example.foncierback.dto.request.MessageProjetRequest;
import com.example.foncierback.dto.request.PropositionProjetRequest;
import com.example.foncierback.dto.request.ReponsePropositionRequest;
import com.example.foncierback.dto.response.ElementFilResponse;
import com.example.foncierback.dto.response.MessageProjetResponse;
import com.example.foncierback.dto.response.PropositionProjetResponse;

import java.util.List;

/**
 * Discussion entre l'acquéreur et la société autour d'un projet de construction :
 * messages libres et propositions de la société, que l'acquéreur accepte ou refuse.
 */
public interface EchangeProjetService {

    /** Messages et propositions du projet, par ordre chronologique. */
    List<ElementFilResponse> fil(Long projetId);

    MessageProjetResponse envoyerMessage(Long projetId, MessageProjetRequest request);

    PropositionProjetResponse proposer(Long projetId, PropositionProjetRequest request);

    /** L'acquéreur retient la proposition : le projet est accepté. */
    PropositionProjetResponse accepter(Long propositionId);

    PropositionProjetResponse refuser(Long propositionId, ReponsePropositionRequest request);
}
