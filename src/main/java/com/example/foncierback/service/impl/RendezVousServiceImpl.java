package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.dto.request.RendezVousRequest;
import com.example.foncierback.dto.response.RendezVousResponse;
import com.example.foncierback.entity.Acquereur;
import com.example.foncierback.entity.AgentPromoteur;
import com.example.foncierback.entity.BienFoncier;
import com.example.foncierback.entity.Creneau;
import com.example.foncierback.entity.LotProgramme;
import com.example.foncierback.entity.ParcelleIndividuelle;
import com.example.foncierback.entity.RendezVous;
import com.example.foncierback.entity.enums.StatutRendezVous;
import com.example.foncierback.repository.AcquereurRepository;
import com.example.foncierback.repository.AgentPromoteurRepository;
import com.example.foncierback.repository.BienFoncierRepository;
import com.example.foncierback.repository.CreneauRepository;
import com.example.foncierback.repository.RendezVousRepository;
import com.example.foncierback.service.RendezVousService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RendezVousServiceImpl implements RendezVousService {

    private final RendezVousRepository rendezVousRepository;
    private final AcquereurRepository acquereurRepository;
    private final AgentPromoteurRepository agentPromoteurRepository;
    private final BienFoncierRepository bienFoncierRepository;
    private final CreneauRepository creneauRepository;

    @Override
    public RendezVousResponse create(RendezVousRequest request) {
        Acquereur acquereur = null;
        if (request.getAcquereurId() != null) {
            acquereur = acquereurRepository.findById(request.getAcquereurId())
                    .orElseThrow(() -> new ResourceNotFoundException("Acquéreur introuvable avec l'identifiant : " + request.getAcquereurId()));
        }

        AgentPromoteur agent = null;
        if (request.getAgentId() != null) {
            agent = agentPromoteurRepository.findById(request.getAgentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Agent introuvable avec l'identifiant : " + request.getAgentId()));
        }

        BienFoncier bien = null;
        if (request.getBienId() != null) {
            bien = bienFoncierRepository.findById(request.getBienId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bien foncier introuvable avec l'identifiant : " + request.getBienId()));
        }

        Creneau creneau = null;
        if (request.getCreneauId() != null) {
            creneau = creneauRepository.findById(request.getCreneauId())
                    .orElseThrow(() -> new ResourceNotFoundException("Créneau introuvable avec l'identifiant : " + request.getCreneauId()));
        }

        StatutRendezVous statut = request.getStatut() != null ? request.getStatut() : StatutRendezVous.EN_ATTENTE;

        RendezVous rendezVous = RendezVous.builder()
                .dateRendezVous(request.getDateRendezVous())
                .typeRDV(request.getTypeRDV())
                .statut(statut)
                .lieu(request.getLieu())
                .motifRefus(request.getMotifRefus())
                .compteRendu(request.getCompteRendu())
                .acquereur(acquereur)
                .agentPromoteur(agent)
                .bienFoncier(bien)
                .creneau(creneau)
                .build();

        RendezVous saved = rendezVousRepository.save(rendezVous);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public RendezVousResponse getById(Long id) {
        return mapToResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVousResponse> getAll() {
        return rendezVousRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public RendezVousResponse update(Long id, RendezVousRequest request) {
        RendezVous existing = findEntityById(id);

        existing.setDateRendezVous(request.getDateRendezVous());
        existing.setTypeRDV(request.getTypeRDV());
        if (request.getStatut() != null) {
            existing.setStatut(request.getStatut());
        }
        existing.setLieu(request.getLieu());
        existing.setMotifRefus(request.getMotifRefus());
        existing.setCompteRendu(request.getCompteRendu());

        if (request.getAcquereurId() != null) {
            Acquereur acquereur = acquereurRepository.findById(request.getAcquereurId())
                    .orElseThrow(() -> new ResourceNotFoundException("Acquéreur introuvable avec l'identifiant : " + request.getAcquereurId()));
            existing.setAcquereur(acquereur);
        } else {
            existing.setAcquereur(null);
        }

        if (request.getAgentId() != null) {
            AgentPromoteur agent = agentPromoteurRepository.findById(request.getAgentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Agent introuvable avec l'identifiant : " + request.getAgentId()));
            existing.setAgentPromoteur(agent);
        } else {
            existing.setAgentPromoteur(null);
        }

        if (request.getBienId() != null) {
            BienFoncier bien = bienFoncierRepository.findById(request.getBienId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bien foncier introuvable avec l'identifiant : " + request.getBienId()));
            existing.setBienFoncier(bien);
        } else {
            existing.setBienFoncier(null);
        }

        if (request.getCreneauId() != null) {
            Creneau creneau = creneauRepository.findById(request.getCreneauId())
                    .orElseThrow(() -> new ResourceNotFoundException("Créneau introuvable avec l'identifiant : " + request.getCreneauId()));
            existing.setCreneau(creneau);
        } else {
            existing.setCreneau(null);
        }

        RendezVous updated = rendezVousRepository.save(existing);
        return mapToResponse(updated);
    }

    @Override
    public void delete(Long id) {
        RendezVous existing = findEntityById(id);
        rendezVousRepository.delete(existing);
    }

    @Override
    public RendezVousResponse changerStatut(Long id, StatutRendezVous statut, String motifRefus) {
        RendezVous existing = findEntityById(id);
        if (statut == StatutRendezVous.ACCEPTER) {
            existing.accepter();
        } else if (statut == StatutRendezVous.REFUSER) {
            existing.refuser(motifRefus);
        } else {
            existing.setStatut(statut);
            existing.setMotifRefus(motifRefus);
            existing.setDateTraitement(LocalDateTime.now());
        }
        RendezVous saved = rendezVousRepository.save(existing);
        return mapToResponse(saved);
    }

    @Override
    public RendezVousResponse accepter(Long id) {
        RendezVous existing = findEntityById(id);
        existing.accepter();
        RendezVous saved = rendezVousRepository.save(existing);
        return mapToResponse(saved);
    }

    @Override
    public RendezVousResponse refuser(Long id, String motifRefus) {
        RendezVous existing = findEntityById(id);
        existing.refuser(motifRefus);
        RendezVous saved = rendezVousRepository.save(existing);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVousResponse> findByAcquereur(Long acquereurId) {
        return rendezVousRepository.findByAcquereurId(acquereurId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVousResponse> findByAgent(Long agentId) {
        return rendezVousRepository.findByAgentPromoteurId(agentId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVousResponse> findBySociete(Long societeId) {
        return rendezVousRepository.findBySocieteId(societeId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RendezVous findEntityById(Long id) {
        return rendezVousRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rendez-vous introuvable avec l'identifiant : " + id));
    }

    private RendezVousResponse mapToResponse(RendezVous entity) {
        BienFoncier bien = entity.getBienFoncier();
        String bienDesignation = null;
        String programmeNom = null;
        if (bien instanceof LotProgramme lot) {
            bienDesignation = lot.getNumeroLot();
            if (lot.getProgrammeFoncier() != null) {
                programmeNom = lot.getProgrammeFoncier().getNom();
            }
        } else if (bien instanceof ParcelleIndividuelle parcelle) {
            bienDesignation = parcelle.getReference();
        }

        String acquereurNom = null;
        String acquereurTelephone = null;
        if (entity.getAcquereur() != null) {
            acquereurNom = (entity.getAcquereur().getPrenom() + " " + entity.getAcquereur().getNom()).trim();
            acquereurTelephone = entity.getAcquereur().getTelephone();
        }

        String agentNom = null;
        if (entity.getAgentPromoteur() != null) {
            agentNom = (entity.getAgentPromoteur().getPrenom() + " " + entity.getAgentPromoteur().getNom()).trim();
        }

        String creneauHeure = null;
        String creneauHeureDebut = null;
        String creneauHeureFin = null;
        if (entity.getCreneau() != null) {
            creneauHeure = entity.getCreneau().getHeure();
            creneauHeureDebut = entity.getCreneau().getHeureDebut() != null
                    ? entity.getCreneau().getHeureDebut().toString() : null;
            creneauHeureFin = entity.getCreneau().getHeureFin() != null
                    ? entity.getCreneau().getHeureFin().toString() : null;
        }

        return RendezVousResponse.builder()
                .id(entity.getId())
                .dateRendezVous(entity.getDateRendezVous())
                .typeRDV(entity.getTypeRDV())
                .statut(entity.getStatut())
                .lieu(entity.getLieu())
                .motifRefus(entity.getMotifRefus())
                .compteRendu(entity.getCompteRendu())
                .dateTraitement(entity.getDateTraitement())
                .acquereurId(entity.getAcquereur() != null ? entity.getAcquereur().getId() : null)
                .agentId(entity.getAgentPromoteur() != null ? entity.getAgentPromoteur().getId() : null)
                .bienId(bien != null ? bien.getId() : null)
                .creneauId(entity.getCreneau() != null ? entity.getCreneau().getId() : null)
                .bienReference(bien != null ? bien.getReference() : null)
                .bienDesignation(bienDesignation)
                .programmeNom(programmeNom)
                .acquereurNom(acquereurNom)
                .acquereurTelephone(acquereurTelephone)
                .agentNom(agentNom)
                .creneauHeure(creneauHeure)
                .creneauHeureDebut(creneauHeureDebut)
                .creneauHeureFin(creneauHeureFin)
                .build();
    }
}
