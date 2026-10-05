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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rendez-vous")
@RequiredArgsConstructor
@Tag(name = "Rendez-vous", description = "Gestion des rendez-vous et visites")
public class RendezVousController {

    private final RendezVousService rendezVousService;

    @PostMapping
    @Operation(summary = "Créer un nouveau rendez-vous")
    public ResponseEntity<ApiResponse<RendezVousResponse>> create(@Valid @RequestBody RendezVousRequest request) {
        RendezVousResponse response = rendezVousService.create(request);
        return new ResponseEntity<>(new ApiResponse<>(true, "Rendez-vous créé avec succès", response), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un rendez-vous par son ID")
    public ResponseEntity<ApiResponse<RendezVousResponse>> getById(@PathVariable Long id) {
        RendezVousResponse response = rendezVousService.getById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Rendez-vous récupéré avec succès", response));
    }

    @GetMapping
    @Operation(summary = "Lister tous les rendez-vous")
    public ResponseEntity<ApiResponse<List<RendezVousResponse>>> getAll() {
        List<RendezVousResponse> list = rendezVousService.getAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des rendez-vous", list));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un rendez-vous existant")
    public ResponseEntity<ApiResponse<RendezVousResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody RendezVousRequest request
    ) {
        RendezVousResponse response = rendezVousService.update(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Rendez-vous mis à jour avec succès", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un rendez-vous")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        rendezVousService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Rendez-vous supprimé avec succès", null));
    }

    @PatchMapping("/{id}/accepter")
    @Operation(summary = "Accepter un rendez-vous")
    public ResponseEntity<ApiResponse<RendezVousResponse>> accepter(@PathVariable Long id) {
        RendezVousResponse response = rendezVousService.accepter(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Rendez-vous accepté avec succès", response));
    }

    @PatchMapping("/{id}/refuser")
    @Operation(summary = "Refuser un rendez-vous")
    public ResponseEntity<ApiResponse<RendezVousResponse>> refuser(
            @PathVariable Long id,
            @RequestParam(required = false) String motif
    ) {
        RendezVousResponse response = rendezVousService.refuser(id, motif);
        return ResponseEntity.ok(new ApiResponse<>(true, "Rendez-vous refusé avec succès", response));
    }

    @PatchMapping("/{id}/statut")
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
    @Operation(summary = "Lister les rendez-vous d'un acquéreur")
    public ResponseEntity<ApiResponse<List<RendezVousResponse>>> findByAcquereur(@PathVariable Long acquereurId) {
        List<RendezVousResponse> list = rendezVousService.findByAcquereur(acquereurId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Rendez-vous de l'acquéreur", list));
    }

    @GetMapping("/agent/{agentId}")
    @Operation(summary = "Lister les rendez-vous d'un agent")
    public ResponseEntity<ApiResponse<List<RendezVousResponse>>> findByAgent(@PathVariable Long agentId) {
        List<RendezVousResponse> list = rendezVousService.findByAgent(agentId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Rendez-vous de l'agent", list));
    }

    @GetMapping("/societe/{societeId}")
    @Operation(summary = "Lister les rendez-vous d'une société promotrice")
    public ResponseEntity<ApiResponse<List<RendezVousResponse>>> findBySociete(@PathVariable Long societeId) {
        List<RendezVousResponse> list = rendezVousService.findBySociete(societeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Rendez-vous de la société promotrice", list));
    }
}
