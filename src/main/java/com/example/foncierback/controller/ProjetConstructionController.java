package com.example.foncierback.controller;

import com.example.foncierback.common.response.ApiResponse;
import com.example.foncierback.dto.request.AvancementProjetRequest;
import com.example.foncierback.dto.request.ProjetConstructionRequest;
import com.example.foncierback.dto.response.ProjetConstructionResponse;
import com.example.foncierback.service.ProjetConstructionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projets-construction")
@RequiredArgsConstructor
@Tag(name = "Projets de Construction", description = "Demandes, choix des modèles et suivi de l'avancement des constructions")
public class ProjetConstructionController {

    private final ProjetConstructionService projetConstructionService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProjetConstructionResponse>> create(@Valid @RequestBody ProjetConstructionRequest request) {
        return new ResponseEntity<>(new ApiResponse<>(true, "Projet de construction créé avec succès", projetConstructionService.create(request)), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjetConstructionResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Projet récupéré avec succès", projetConstructionService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjetConstructionResponse>>> getAll() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des projets de construction", projetConstructionService.getAll()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjetConstructionResponse>> update(@PathVariable Long id, @Valid @RequestBody ProjetConstructionRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Projet mis à jour avec succès", projetConstructionService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        projetConstructionService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Projet supprimé avec succès", null));
    }

    @PatchMapping("/{id}/valider")
    public ResponseEntity<ApiResponse<ProjetConstructionResponse>> valider(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Projet validé avec succès", projetConstructionService.valider(id)));
    }

    @PatchMapping("/{id}/refuser")
    public ResponseEntity<ApiResponse<ProjetConstructionResponse>> refuser(@PathVariable Long id, @RequestParam(required = false) String motif) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Projet refusé avec succès", projetConstructionService.refuser(id, motif)));
    }

    @PatchMapping("/{id}/modele/{modelMaisonId}")
    @Operation(summary = "Associer un modèle de maison compatible à un projet")
    public ResponseEntity<ApiResponse<ProjetConstructionResponse>> changerModele(
            @PathVariable Long id, @PathVariable Long modelMaisonId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Modèle du projet mis à jour avec succès",
                projetConstructionService.changerModele(id, modelMaisonId)));
    }

    @PatchMapping("/{id}/avancement")
    @Operation(summary = "Mettre à jour l'avancement d'un projet")
    public ResponseEntity<ApiResponse<ProjetConstructionResponse>> avancement(
            @PathVariable Long id,
            @Valid @RequestBody AvancementProjetRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Avancement mis à jour avec succès", projetConstructionService.mettreAJourAvancement(id, request)));
    }

    @GetMapping("/acquereur/{acquereurId}")
    public ResponseEntity<ApiResponse<List<ProjetConstructionResponse>>> findByAcquereur(@PathVariable Long acquereurId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Projets de construction de l'acquéreur", projetConstructionService.findByAcquereur(acquereurId)));
    }

    @GetMapping("/societe/{societeId}")
    public ResponseEntity<ApiResponse<List<ProjetConstructionResponse>>> findBySociete(@PathVariable Long societeId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Projets de construction de la société", projetConstructionService.findBySociete(societeId)));
    }
}
