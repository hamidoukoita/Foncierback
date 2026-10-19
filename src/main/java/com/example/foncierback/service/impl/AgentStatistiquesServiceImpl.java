package com.example.foncierback.service.impl;

import com.example.foncierback.config.security.SecurityUtils;
import com.example.foncierback.dto.response.AgentPromoteurResponse;
import com.example.foncierback.dto.response.AgentStatistiquesResponse;
import com.example.foncierback.dto.response.PageResponse;
import com.example.foncierback.dto.response.RendezVousResponse;
import com.example.foncierback.dto.response.ReservationResponse;
import com.example.foncierback.entity.enums.PeriodeStatistiques;
import com.example.foncierback.entity.enums.StatutRendezVous;
import com.example.foncierback.entity.enums.StatutReservation;
import com.example.foncierback.entity.enums.SuiteVente;
import com.example.foncierback.repository.RendezVousRepository;
import com.example.foncierback.repository.ReservationRepository;
import com.example.foncierback.service.AgentPromoteurService;
import com.example.foncierback.service.AgentStatistiquesService;
import com.example.foncierback.service.RendezVousService;
import com.example.foncierback.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Indicateurs d'un agent calculés par la base (comptages, sommes, moyennes) : le temps de réponse
 * ne dépend pas du nombre de dossiers. L'historique est servi à part, page par page.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgentStatistiquesServiceImpl implements AgentStatistiquesService {

    /** Début de « depuis toujours » : antérieur à toute donnée de la plateforme. */
    private static final LocalDateTime ORIGINE = LocalDateTime.of(2000, 1, 1, 0, 0);
    private static final int TAILLE_MAX_PAGE = 50;
    private static final Set<SuiteVente> SUITES_REMBOURSEES = EnumSet.of(SuiteVente.REMBOURSEMENT_EN_COURS, SuiteVente.REMBOURSEE);

    private final AgentPromoteurService agentPromoteurService;
    private final ReservationRepository reservationRepository;
    private final RendezVousRepository rendezVousRepository;
    private final ReservationService reservationService;
    private final RendezVousService rendezVousService;

    @Override
    public AgentStatistiquesResponse statistiques(Long agentId, PeriodeStatistiques periode) {
        AgentPromoteurResponse agent = agentAccessible(agentId);
        LocalDateTime debut = debut(periode);
        return AgentStatistiquesResponse.builder()
                .periode(periode)
                .agent(agent)
                .reservations(indicateursReservations(agentId, debut))
                .visites(indicateursVisites(agentId, debut))
                .activite(new AgentStatistiquesResponse.Activite(
                        reservationRepository.chiffreAffairesAgent(agentId, debut, StatutReservation.VENDUE),
                        reservationRepository.reservationsEncaisseesAgent(agentId, debut, SUITES_REMBOURSEES)))
                .reactivite(new AgentStatistiquesResponse.Reactivite(
                        heures(reservationRepository.delaiMoyenPriseEnChargeMinutes(agentId, debut)),
                        heures(reservationRepository.delaiMoyenVenteMinutes(agentId, debut))))
                .build();
    }

    @Override
    public PageResponse<ReservationResponse> dossiers(Long agentId, PeriodeStatistiques periode, int page, int taille) {
        agentAccessible(agentId);
        return reservationService.pageParAgent(agentId, debut(periode), Math.max(page, 0), taille(taille));
    }

    @Override
    public PageResponse<RendezVousResponse> visites(Long agentId, PeriodeStatistiques periode, int page, int taille) {
        agentAccessible(agentId);
        return rendezVousService.pageParAgent(agentId, debut(periode), Math.max(page, 0), taille(taille));
    }

    /** L'administrateur, le responsable de la société de l'agent ou l'agent lui-même. */
    private AgentPromoteurResponse agentAccessible(Long agentId) {
        AgentPromoteurResponse agent = agentPromoteurService.getById(agentId);
        if (SecurityUtils.isAdmin() || Objects.equals(SecurityUtils.getCurrentUserId(), agent.getId())) return agent;
        if (!SecurityUtils.isResponsableSociete() || !Objects.equals(SecurityUtils.getCurrentSocieteId(), agent.getSocieteId())) {
            throw new AccessDeniedException("Vous ne pouvez consulter que les agents de votre société.");
        }
        return agent;
    }

    private static LocalDateTime debut(PeriodeStatistiques periode) {
        return periode.getJours() > 0 ? LocalDateTime.now().minusDays(periode.getJours()) : ORIGINE;
    }

    private static int taille(int demandee) {
        return Math.min(Math.max(demandee, 1), TAILLE_MAX_PAGE);
    }

    private AgentStatistiquesResponse.Reservations indicateursReservations(Long agentId, LocalDateTime debut) {
        Map<StatutReservation, Long> parStatut = new EnumMap<>(StatutReservation.class);
        for (Object[] ligne : reservationRepository.compterParStatutPourAgent(agentId, debut)) {
            parStatut.put((StatutReservation) ligne[0], (Long) ligne[1]);
        }
        long total = parStatut.values().stream().mapToLong(Long::longValue).sum();
        long vendues = parStatut.getOrDefault(StatutReservation.VENDUE, 0L);
        return AgentStatistiquesResponse.Reservations.builder()
                .attribuees(total)
                .enAttente(parStatut.getOrDefault(StatutReservation.EN_ATTENTE, 0L))
                // Anciens dossiers « CONFIRMER » : rattachés aux dossiers en cours, comme ailleurs.
                .enCours(parStatut.getOrDefault(StatutReservation.EN_COURS, 0L) + parStatut.getOrDefault(StatutReservation.CONFIRMER, 0L))
                .vendues(vendues)
                .expirees(parStatut.getOrDefault(StatutReservation.EXPIREE, 0L))
                .annulees(parStatut.getOrDefault(StatutReservation.ANNULEE, 0L) + parStatut.getOrDefault(StatutReservation.REFUSER, 0L))
                .tauxConversion(total == 0 ? null : (int) Math.round(vendues * 100.0 / total))
                .build();
    }

    private AgentStatistiquesResponse.Visites indicateursVisites(Long agentId, LocalDateTime debut) {
        Map<StatutRendezVous, Long> parStatut = new EnumMap<>(StatutRendezVous.class);
        for (Object[] ligne : rendezVousRepository.compterParStatutPourAgent(agentId, debut)) {
            parStatut.put((StatutRendezVous) ligne[0], (Long) ligne[1]);
        }
        long acceptees = parStatut.getOrDefault(StatutRendezVous.ACCEPTER, 0L);
        long effectuees = rendezVousRepository.compterEffectueesPourAgent(agentId, debut, StatutRendezVous.ACCEPTER, LocalDateTime.now());
        return AgentStatistiquesResponse.Visites.builder()
                .attribuees(parStatut.values().stream().mapToLong(Long::longValue).sum())
                .enAttente(parStatut.getOrDefault(StatutRendezVous.EN_ATTENTE, 0L))
                .prevues(acceptees - effectuees)
                .effectuees(effectuees)
                .refusees(parStatut.getOrDefault(StatutRendezVous.REFUSER, 0L))
                .build();
    }

    private static Double heures(Double minutes) {
        return minutes == null ? null : Math.round(minutes / 6.0) / 10.0;
    }
}
