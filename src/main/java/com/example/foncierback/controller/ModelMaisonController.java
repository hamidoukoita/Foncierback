package com.example.foncierback.controller;

import com.example.foncierback.common.response.ApiResponse;
import com.example.foncierback.dto.request.ModelMaisonRequest;
import com.example.foncierback.dto.response.ModelMaisonResponse;
import com.example.foncierback.service.ModelMaisonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modeles-maison")
@RequiredArgsConstructor
@Tag(name = "Modèles Maison", description = "Gestion des modèles de maison de construction")
public class ModelMaisonController {

    private final ModelMaisonService modelMaisonService;

    @PostMapping
    @Operation(summary = "Créer un nouveau modèle de maison")
    public ResponseEntity<ApiResponse<ModelMaisonResponse>> create(@Valid @RequestBody ModelMaisonRequest request) {
        ModelMaisonResponse response = modelMaisonService.create(request);
        return new ResponseEntity<>(new ApiResponse<>(true, "Modèle de maison créé avec succès", response), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un modèle de maison par son ID")
    public ResponseEntity<ApiResponse<ModelMaisonResponse>> getById(@PathVariable Long id) {
        ModelMaisonResponse response = modelMaisonService.getById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Modèle de maison récupéré avec succès", response));
    }

    @GetMapping
    @Operation(summary = "Lister tous les modèles de maison")
    public ResponseEntity<ApiResponse<List<ModelMaisonResponse>>> getAll() {
        List<ModelMaisonResponse> list = modelMaisonService.getAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des modèles de maison", list));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un modèle de maison existant")
    public ResponseEntity<ApiResponse<ModelMaisonResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ModelMaisonRequest request
    ) {
        ModelMaisonResponse response = modelMaisonService.update(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Modèle de maison mis à jour avec succès", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un modèle de maison")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        modelMaisonService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Modèle de maison supprimé avec succès", null));
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des modèles de maison par mot-clé dans le libellé")
    public ResponseEntity<ApiResponse<List<ModelMaisonResponse>>> search(@RequestParam String keyword) {
        List<ModelMaisonResponse> list = modelMaisonService.searchByLibeller(keyword);
        return ResponseEntity.ok(new ApiResponse<>(true, "Résultats de recherche", list));
    }
}
