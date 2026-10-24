package com.example.foncierback.controller;

import com.example.foncierback.common.response.ApiResponse;
import com.example.foncierback.dto.request.MessageProjetRequest;
import com.example.foncierback.dto.request.PropositionProjetRequest;
import com.example.foncierback.dto.request.ReponsePropositionRequest;
import com.example.foncierback.dto.response.ElementFilResponse;
import com.example.foncierback.dto.response.FichierProjetResponse;
import com.example.foncierback.dto.response.MessageProjetResponse;
import com.example.foncierback.dto.response.PropositionProjetResponse;
import com.example.foncierback.service.EchangeProjetService;
import com.example.foncierback.service.FichierProjetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/** Discussion acquéreur ↔ société sur un projet de construction (droits fins vérifiés par le service). */
@RestController
@RequestMapping("/api/projets-construction")
@RequiredArgsConstructor
@Tag(name = "Échanges des projets de construction", description = "Messages et propositions entre l'acquéreur et la société")
public class EchangeProjetController {

    private final EchangeProjetService echangeService;
    private final FichierProjetService fichierProjetService;

    @GetMapping("/{id}/fil")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'ACQUEREUR')")
    @Operation(summary = "Fil d'échange du projet (messages et propositions, par ordre chronologique)")
    public ResponseEntity<ApiResponse<List<ElementFilResponse>>> fil(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Fil d'échange du projet", echangeService.fil(id)));
    }

    @PostMapping("/{id}/messages")
    @PreAuthorize("hasAnyRole('AGENT', 'ACQUEREUR')")
    @Operation(summary = "Écrire à l'autre partie")
    public ResponseEntity<ApiResponse<MessageProjetResponse>> envoyerMessage(
            @PathVariable Long id, @Valid @RequestBody MessageProjetRequest request) {
        return new ResponseEntity<>(new ApiResponse<>(true, "Message envoyé",
                echangeService.envoyerMessage(id, request)), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/propositions")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Operation(summary = "Faire une proposition de construction à l'acquéreur")
    public ResponseEntity<ApiResponse<PropositionProjetResponse>> proposer(
            @PathVariable Long id, @Valid @RequestBody PropositionProjetRequest request) {
        return new ResponseEntity<>(new ApiResponse<>(true, "Proposition envoyée à l'acquéreur",
                echangeService.proposer(id, request)), HttpStatus.CREATED);
    }

    @PostMapping(value = "/propositions/{propositionId}/plans", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Operation(summary = "Joindre un plan à une proposition (PDF ou image, 15 Mo max)")
    public ResponseEntity<ApiResponse<FichierProjetResponse>> ajouterPlan(
            @PathVariable Long propositionId, @RequestParam("fichier") MultipartFile fichier) {
        return new ResponseEntity<>(new ApiResponse<>(true, "Plan joint à la proposition",
                fichierProjetService.ajouterPlanProposition(propositionId, fichier)), HttpStatus.CREATED);
    }

    @PatchMapping("/propositions/{propositionId}/accepter")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACQUEREUR')")
    @Operation(summary = "Accepter une proposition : le projet est accepté")
    public ResponseEntity<ApiResponse<PropositionProjetResponse>> accepter(@PathVariable Long propositionId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Proposition acceptée : votre projet est lancé",
                echangeService.accepter(propositionId)));
    }

    @PatchMapping("/propositions/{propositionId}/refuser")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACQUEREUR')")
    @Operation(summary = "Décliner une proposition en expliquant pourquoi")
    public ResponseEntity<ApiResponse<PropositionProjetResponse>> refuser(
            @PathVariable Long propositionId, @Valid @RequestBody(required = false) ReponsePropositionRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Proposition déclinée",
                echangeService.refuser(propositionId, request)));
    }
}
