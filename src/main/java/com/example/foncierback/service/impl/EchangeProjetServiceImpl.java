package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.BadRequestException;
import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.config.security.SecurityUtils;
import com.example.foncierback.dto.request.MessageProjetRequest;
import com.example.foncierback.dto.request.PropositionProjetRequest;
import com.example.foncierback.dto.request.ReponsePropositionRequest;
import com.example.foncierback.dto.response.ElementFilResponse;
import com.example.foncierback.dto.response.MessageProjetResponse;
import com.example.foncierback.dto.response.PropositionProjetResponse;
import com.example.foncierback.entity.MessageProjet;
import com.example.foncierback.entity.ModelMaison;
import com.example.foncierback.entity.ProjetConstruction;
import com.example.foncierback.entity.PropositionProjet;
import com.example.foncierback.entity.Utilisateur;
import com.example.foncierback.entity.enums.CoteEchange;
import com.example.foncierback.entity.enums.StatutProjet;
import com.example.foncierback.entity.enums.StatutProposition;
import com.example.foncierback.repository.MessageProjetRepository;
import com.example.foncierback.repository.ModelMaisonRepository;
import com.example.foncierback.repository.ProjetConstructionRepository;
import com.example.foncierback.repository.PropositionProjetRepository;
import com.example.foncierback.repository.UtilisateurRepository;
import com.example.foncierback.service.EchangeProjetService;
import com.example.foncierback.service.ProjetConstructionAcces;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.example.foncierback.service.impl.ProjetConstructionServiceImpl.CIBLE_PROJET;
import static com.example.foncierback.service.impl.ProjetConstructionServiceImpl.LIBELLE_PROJET;
import static com.example.foncierback.service.impl.ProjetConstructionServiceImpl.TYPE_PROJET;

@Service
@RequiredArgsConstructor
@Transactional
public class EchangeProjetServiceImpl implements EchangeProjetService {

    private final ProjetConstructionRepository projetRepository;
    private final MessageProjetRepository messageRepository;
    private final PropositionProjetRepository propositionRepository;
    private final ModelMaisonRepository modelMaisonRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ProjetConstructionAcces acces;
    private final NotificationDispatchService notifications;

    @Override
    @Transactional(readOnly = true)
    public List<ElementFilResponse> fil(Long projetId) {
        ProjetConstruction projet = projet(projetId);
        acces.verifierLecture(projet);
        List<ElementFilResponse> fil = new ArrayList<>();
        messageRepository.findByProjetIdOrderByDateEnvoiAsc(projetId).forEach(m -> fil.add(ElementFilResponse.builder()
                .type(ElementFilResponse.MESSAGE).date(m.getDateEnvoi()).message(versReponse(m)).build()));
        propositionRepository.findByProjetIdOrderByDatePropositionAsc(projetId).forEach(p -> fil.add(ElementFilResponse.builder()
                .type(ElementFilResponse.PROPOSITION).date(p.getDateProposition()).proposition(versReponse(p)).build()));
        fil.sort(Comparator.comparing(ElementFilResponse::getDate));
        return fil;
    }

    @Override
    public MessageProjetResponse envoyerMessage(Long projetId, MessageProjetRequest request) {
        ProjetConstruction projet = projet(projetId);
        CoteEchange cote = coteDeLUtilisateur(projet);
        if (projet.getStatut() == StatutProjet.REFUSER) {
            throw new BadRequestException("Ce projet a été refusé : la discussion est close.");
        }
        MessageProjet message = messageRepository.save(MessageProjet.builder()
                .projet(projet)
                .auteur(utilisateurCourant())
                .cote(cote)
                .texte(request.getTexte().trim())
                .dateEnvoi(LocalDateTime.now())
                .build());

        if (cote == CoteEchange.SOCIETE) {
            ouvrirDiscussion(projet);
            notifications.notifyUser(projet.getAcquereur(), TYPE_PROJET, LIBELLE_PROJET,
                    "Nouveau message de " + nomSociete(projet), apercu(message.getTexte()), CIBLE_PROJET, projet.getId());
        } else {
            notifierSociete(projet, "Nouveau message de " + nomAcquereur(projet), apercu(message.getTexte()));
        }
        projet.setDateDerniereMiseAJour(LocalDateTime.now());
        return versReponse(message);
    }

    @Override
    public PropositionProjetResponse proposer(Long projetId, PropositionProjetRequest request) {
        ProjetConstruction projet = projet(projetId);
        acces.verifierGestion(projet);
        if (!projet.isEnCours()) throw new BadRequestException("Ce projet a déjà été traité : il n'accepte plus de proposition.");

        ModelMaison modele = null;
        if (request.getModelMaisonId() != null) {
            modele = modelMaisonRepository.findById(request.getModelMaisonId())
                    .orElseThrow(() -> new ResourceNotFoundException("Modèle de maison introuvable avec l'identifiant : "
                            + request.getModelMaisonId()));
            if (modele.getSocietePromotrice() != null && projet.getSocietePromotrice() != null
                    && !Objects.equals(modele.getSocietePromotrice().getId(), projet.getSocietePromotrice().getId())) {
                throw new BadRequestException("Ce modèle n'appartient pas à votre catalogue.");
            }
        }
        if (modele == null && !DescriptionMaisonMapper.estRenseignee(request.getMaison())) {
            throw new BadRequestException("Choisissez un modèle du catalogue ou décrivez la maison proposée.");
        }

        PropositionProjet proposition = propositionRepository.save(PropositionProjet.builder()
                .projet(projet)
                .numero((int) propositionRepository.countByProjetId(projetId) + 1)
                .titre(request.getTitre().trim())
                .modelMaison(modele)
                .maison(DescriptionMaisonMapper.versEntite(request.getMaison()))
                .prixEstime(request.getPrixEstime())
                .delaiMois(request.getDelaiMois())
                .commentaire(nettoyer(request.getCommentaire()))
                .auteur(utilisateurCourant())
                .dateProposition(LocalDateTime.now())
                .build());

        ouvrirDiscussion(projet);
        projet.setDateDerniereMiseAJour(LocalDateTime.now());
        notifications.notifyUser(projet.getAcquereur(), TYPE_PROJET, LIBELLE_PROJET,
                nomSociete(projet) + " vous fait une proposition",
                "Proposition n°" + proposition.getNumero() + " : " + proposition.getTitre()
                        + ". Consultez-la et donnez votre réponse.",
                CIBLE_PROJET, projet.getId());
        return versReponse(proposition);
    }

    @Override
    public PropositionProjetResponse accepter(Long propositionId) {
        PropositionProjet proposition = propositionEnAttente(propositionId);
        ProjetConstruction projet = proposition.getProjet();

        proposition.repondre(StatutProposition.ACCEPTEE, null);
        propositionRepository.findByProjetIdAndStatut(projet.getId(), StatutProposition.EN_ATTENTE).stream()
                .filter(autre -> !autre.getId().equals(proposition.getId()))
                .forEach(autre -> autre.repondre(StatutProposition.CADUQUE, null));
        projet.accepterProposition(proposition);

        notifierSociete(projet, "Proposition acceptée",
                nomAcquereur(projet) + " accepte la proposition n°" + proposition.getNumero() + " (« "
                        + proposition.getTitre() + " ») : le projet " + projet.getNumeroDossier() + " est accepté.");
        return versReponse(proposition);
    }

    @Override
    public PropositionProjetResponse refuser(Long propositionId, ReponsePropositionRequest request) {
        PropositionProjet proposition = propositionEnAttente(propositionId);
        String commentaire = request == null ? null : nettoyer(request.getCommentaire());
        proposition.repondre(StatutProposition.REFUSEE, commentaire);
        proposition.getProjet().setDateDerniereMiseAJour(LocalDateTime.now());

        notifierSociete(proposition.getProjet(), "Proposition déclinée",
                nomAcquereur(proposition.getProjet()) + " décline la proposition n°" + proposition.getNumero()
                        + (commentaire == null ? "." : " : « " + apercu(commentaire) + " »"));
        return versReponse(proposition);
    }

    // ------------------------------------------------------------------ Outils

    private PropositionProjet propositionEnAttente(Long propositionId) {
        PropositionProjet proposition = propositionRepository.findById(propositionId)
                .orElseThrow(() -> new ResourceNotFoundException("Proposition introuvable avec l'identifiant : " + propositionId));
        acces.verifierProprietaire(proposition.getProjet());
        if (proposition.getStatut() != StatutProposition.EN_ATTENTE || !proposition.getProjet().isEnCours()) {
            throw new BadRequestException("Cette proposition n'attend plus de réponse.");
        }
        return proposition;
    }

    /** Côté de la personne connectée ; l'administration consulte le fil mais n'y écrit pas. */
    private CoteEchange coteDeLUtilisateur(ProjetConstruction projet) {
        if (acces.estProprietaire(projet)) return CoteEchange.ACQUEREUR;
        if (acces.estSocieteDestinataire(projet)) return CoteEchange.SOCIETE;
        throw new AccessDeniedException("Seuls l'acquéreur et la société du projet échangent dans cette discussion.");
    }

    /** La première réponse de la société fait passer le dossier « en discussion ». */
    private void ouvrirDiscussion(ProjetConstruction projet) {
        if (projet.getStatut() == StatutProjet.EN_ETUDE) {
            projet.setStatut(StatutProjet.EN_DISCUSSION);
            projet.setEtapeAvancement("En discussion");
        }
    }

    private void notifierSociete(ProjetConstruction projet, String titre, String message) {
        // Le responsable est prévenu par notifySociete ; un agent collaborateur chargé du dossier l'est aussi.
        if (projet.getAgentPromoteur() != null && !projet.getAgentPromoteur().isEstResponsableSociete()) {
            notifications.notifyUser(projet.getAgentPromoteur(), TYPE_PROJET, LIBELLE_PROJET, titre, message,
                    CIBLE_PROJET, projet.getId());
        }
        notifications.notifySociete(projet.getSocietePromotrice(), TYPE_PROJET, LIBELLE_PROJET, titre, message,
                CIBLE_PROJET, projet.getId());
    }

    private ProjetConstruction projet(Long id) {
        return projetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projet de construction introuvable avec l'identifiant : " + id));
    }

    private Utilisateur utilisateurCourant() {
        Long id = SecurityUtils.getCurrentUserId();
        return id == null ? null : utilisateurRepository.findById(id).orElse(null);
    }

    private static String nomSociete(ProjetConstruction projet) {
        return projet.getSocietePromotrice() == null ? "La société" : projet.getSocietePromotrice().getNom();
    }

    private static String nomAcquereur(ProjetConstruction projet) {
        return projet.getAcquereur() == null ? "L'acquéreur" : nomComplet(projet.getAcquereur());
    }

    private static String nomComplet(Utilisateur u) {
        return ((u.getPrenom() == null ? "" : u.getPrenom()) + " " + (u.getNom() == null ? "" : u.getNom())).trim();
    }

    private static String apercu(String texte) {
        return texte.length() <= 140 ? texte : texte.substring(0, 137) + "…";
    }

    private static String nettoyer(String v) {
        if (v == null) return null;
        String t = v.trim();
        return t.isEmpty() ? null : t;
    }

    static MessageProjetResponse versReponse(MessageProjet m) {
        return MessageProjetResponse.builder()
                .id(m.getId())
                .texte(m.getTexte())
                .cote(m.getCote())
                .auteurNom(m.getAuteur() == null ? null : nomComplet(m.getAuteur()))
                .dateEnvoi(m.getDateEnvoi())
                .build();
    }

    static PropositionProjetResponse versReponse(PropositionProjet p) {
        ModelMaison modele = p.getModelMaison();
        return PropositionProjetResponse.builder()
                .id(p.getId())
                .projetId(p.getProjet().getId())
                .numero(p.getNumero())
                .titre(p.getTitre())
                .maison(DescriptionMaisonMapper.versDto(p.getMaison()))
                .modelMaisonId(modele == null ? null : modele.getId())
                .modelMaisonLibeller(modele == null ? null : modele.getLibeller())
                .modelMaisonImageUrl(modele == null ? null : modele.getImageUrl())
                .prixEstime(p.getPrixEstime())
                .delaiMois(p.getDelaiMois())
                .commentaire(p.getCommentaire())
                .statut(p.getStatut())
                .reponseAcquereur(p.getReponseAcquereur())
                .dateProposition(p.getDateProposition())
                .dateReponse(p.getDateReponse())
                .auteurNom(p.getAuteur() == null ? null : nomComplet(p.getAuteur()))
                .plans(p.getProjet().getFichiers().stream()
                        .filter(f -> f.getProposition() != null && f.getProposition().getId().equals(p.getId()))
                        .map(FichierProjetServiceImpl::versReponse)
                        .toList())
                .build();
    }
}
