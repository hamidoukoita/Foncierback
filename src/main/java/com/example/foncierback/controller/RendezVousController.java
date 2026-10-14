package com.example.foncierback.controller;

import com.example.foncierback.common.response.ApiResponse;
import com.example.foncierback.dto.request.RendezVousRequest;
import com.example.foncierback.dto.response.RendezVousResponse;
import com.example.foncierback.entity.enums.StatutRendezVous;
import com.example.foncierback.service.RendezVousService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rendez-vous")
@RequiredArgsConstructor
@Tag(name = "Rendez-vous", description = "Gestion des rendez-vous et visites")
public class RendezVousController {

    private final RendezVousService rendezVousService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or (hasRole('ACQUEREUR') and @securityUtils.isOwnerOrAdmin(#request.acquereurId))")
    @Operation(summary = "Créer un nouveau rendez-vous")
    public ResponseEntity<ApiResponse<RendezVousResponse>> create(@Valid @RequestBody RendezVousRequest request) {
        RendezVousResponse response = rendezVousService.create(request);
        return new ResponseEntity<>(new ApiResponse<>(true, "Rendez-vous créé avec succès", response), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'ACQUEREUR')")
    @Operation(summary = "Récupérer un rendez-vous par son ID")
    public ResponseEntity<ApiResponse<RendezVousResponse>> getById(@PathVariable Long id) {
        RendezVousResponse response = rendezVousService.getById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Rendez-vous récupéré avec succès", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Operation(summary = "Lister tous les rendez-vous")
    public ResponseEntity<ApiResponse<List<RendezVousResponse>>> getAll() {
        List<RendezVousResponse> list = rendezVousService.getAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des rendez-vous", list));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Operation(summary = "Modifier un rendez-vous existant")
    public ResponseEntity<ApiResponse<RendezVousResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody RendezVousRequest request
    ) {
        RendezVousResponse response = rendezVousService.update(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Rendez-vous mis à jour avec succès", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer un rendez-vous")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        rendezVousService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Rendez-vous supprimé avec succès", null));
    }


    @PatchMapping("/{id}/agent/{agentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Operation(summary = "Attribuer un agent à un rendez-vous (passe le statut à ACCEPTER)")
    public ResponseEntity<ApiResponse<RendezVousResponse>> assignerAgent(
            @PathVariable Long id,
            @PathVariable Long agentId
    ) {
        RendezVousResponse response = rendezVousService.assignerAgent(id, agentId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Agent attribué et rendez-vous confirmé", response));
    }

    @PatchMapping("/{id}/accepter")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Operation(summary = "Accepter un rendez-vous")
    public ResponseEntity<ApiResponse<RendezVousResponse>> accepter(@PathVariable Long id) {
        RendezVousResponse response = rendezVousService.accepter(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Rendez-vous accepté avec succès", response));
    }

    @PatchMapping("/{id}/refuser")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Operation(summary = "Refuser un rendez-vous")
    public ResponseEntity<ApiResponse<RendezVousResponse>> refuser(
            @PathVariable Long id,
            @RequestParam(required = false) String motif
    ) {
        RendezVousResponse response = rendezVousService.refuser(id, motif);
        return ResponseEntity.ok(new ApiResponse<>(true, "Rendez-vous refusé avec succès", response));
    }

    @PatchMapping("/{id}/annuler")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACQUEREUR')")
    @Operation(summary = "Annuler son rendez-vous (acquéreur), tant qu'il n'est pas passé")
    public ResponseEntity<ApiResponse<RendezVousResponse>> annuler(@PathVariable Long id) {
        RendezVousResponse response = rendezVousService.annulerParAcquereur(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Rendez-vous annulé avec succès", response));
    }

    @PatchMapping("/{id}/statut")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Operation(summary = "Changer le statut d'un rendez-vous")
    public ResponseEntity<ApiResponse<RendezVousResponse>> changerStatut(
            @PathVariable Long id,
            @RequestParam StatutRendezVous statut,
            @RequestParam(required = false) String motifRefus
    ) {
        RendezVousResponse response = rendezVousService.changerStatut(id, statut, motifRefus);
        return ResponseEntity.ok(new ApiResponse<>(true, "Statut du rendez-vous mis à jour avec succès", response));
    }

    @GetMapping("/acquereur/{acquereurId}")
    @PreAuthorize("hasRole('ADMIN') or @securityUtils.isOwnerOrAdmin(#acquereurId)")
    @Operation(summary = "Lister les rendez-vous d'un acquéreur")
    public ResponseEntity<ApiResponse<List<RendezVousResponse>>> findByAcquereur(@PathVariable Long acquereurId) {
        List<RendezVousResponse> list = rendezVousService.findByAcquereur(acquereurId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Rendez-vous de l'acquéreur", list));
    }

    @GetMapping("/agent/{agentId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('AGENT') and @securityUtils.isOwnerOrAdmin(#agentId))")
    @Operation(summary = "Lister les rendez-vous d'un agent")
    public ResponseEntity<ApiResponse<List<RendezVousResponse>>> findByAgent(@PathVariable Long agentId) {
        List<RendezVousResponse> list = rendezVousService.findByAgent(agentId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Rendez-vous de l'agent", list));
    }

    @GetMapping("/societe/{societeId}")
    @PreAuthorize("hasRole('ADMIN') or @securityUtils.belongsToSocieteOrAdmin(#societeId)")
    @Operation(summary = "Lister les rendez-vous d'une société promotrice")
    public ResponseEntity<ApiResponse<List<RendezVousResponse>>> findBySociete(@PathVariable Long societeId) {
        List<RendezVousResponse> list = rendezVousService.findBySociete(societeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Rendez-vous de la société promotrice", list));
    }
}
