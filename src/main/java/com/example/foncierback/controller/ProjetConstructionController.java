package com.example.foncierback.controller;

import com.example.foncierback.common.response.ApiResponse;
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
@Tag(name = "Projets de Construction", description = "Gestion des demandes et projets de construction")
public class ProjetConstructionController {

    private final ProjetConstructionService projetConstructionService;

    @PostMapping
    @Operation(summary = "Créer une nouvelle demande de projet de construction")
    public ResponseEntity<ApiResponse<ProjetConstructionResponse>> create(@Valid @RequestBody ProjetConstructionRequest request) {
        ProjetConstructionResponse response = projetConstructionService.create(request);
        return new ResponseEntity<>(new ApiResponse<>(true, "Projet de construction créé avec succès", response), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un projet de construction par son ID")
    public ResponseEntity<ApiResponse<ProjetConstructionResponse>> getById(@PathVariable Long id) {
        ProjetConstructionResponse response = projetConstructionService.getById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Projet de construction récupéré avec succès", response));
    }

    @GetMapping
    @Operation(summary = "Lister tous les projets de construction")
    public ResponseEntity<ApiResponse<List<ProjetConstructionResponse>>> getAll() {
        List<ProjetConstructionResponse> list = projetConstructionService.getAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des projets de construction", list));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un projet de construction existant")
    public ResponseEntity<ApiResponse<ProjetConstructionResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ProjetConstructionRequest request
    ) {
        ProjetConstructionResponse response = projetConstructionService.update(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Projet de construction mis à jour avec succès", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un projet de construction")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        projetConstructionService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Projet de construction supprimé avec succès", null));
    }

    @PatchMapping("/{id}/valider")
    @Operation(summary = "Valider un projet de construction")
    public ResponseEntity<ApiResponse<ProjetConstructionResponse>> valider(@PathVariable Long id) {
        ProjetConstructionResponse response = projetConstructionService.valider(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Projet de construction validé avec succès", response));
    }

    @PatchMapping("/{id}/refuser")
    @Operation(summary = "Refuser un projet de construction")
    public ResponseEntity<ApiResponse<ProjetConstructionResponse>> refuser(
            @PathVariable Long id,
            @RequestParam(required = false) String motif
    ) {
        ProjetConstructionResponse response = projetConstructionService.refuser(id, motif);
        return ResponseEntity.ok(new ApiResponse<>(true, "Projet de construction refusé avec succès", response));
    }

    @GetMapping("/acquereur/{acquereurId}")
    @Operation(summary = "Lister les projets de construction d'un acquéreur")
    public ResponseEntity<ApiResponse<List<ProjetConstructionResponse>>> findByAcquereur(@PathVariable Long acquereurId) {
        List<ProjetConstructionResponse> list = projetConstructionService.findByAcquereur(acquereurId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Projets de construction de l'acquéreur", list));
    }

    @GetMapping("/societe/{societeId}")
    @Operation(summary = "Lister les projets de construction d'une société promotrice")
    public ResponseEntity<ApiResponse<List<ProjetConstructionResponse>>> findBySociete(@PathVariable Long societeId) {
        List<ProjetConstructionResponse> list = projetConstructionService.findBySociete(societeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Projets de construction de la société", list));
    }
}
