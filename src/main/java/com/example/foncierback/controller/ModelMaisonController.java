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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/modeles-maison")
@RequiredArgsConstructor
@Tag(name = "Modèles Maison", description = "Catalogue des modèles de maison proposés aux acquéreurs")
public class ModelMaisonController {

    private final ModelMaisonService modelMaisonService;

    @PostMapping
    public ResponseEntity<ApiResponse<ModelMaisonResponse>> create(@Valid @RequestBody ModelMaisonRequest request) {
        return new ResponseEntity<>(new ApiResponse<>(true, "Modèle de maison créé avec succès", modelMaisonService.create(request)), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ModelMaisonResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Modèle récupéré avec succès", modelMaisonService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ModelMaisonResponse>>> getAll(@RequestParam(required = false) Long societeId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des modèles de maison", modelMaisonService.getAll(societeId)));
    }

    @GetMapping("/compatibles")
    @Operation(summary = "Lister les modèles compatibles avec un terrain")
    public ResponseEntity<ApiResponse<List<ModelMaisonResponse>>> compatibles(
            @RequestParam(required = false) Double surfaceTerrain,
            @RequestParam(required = false) String typeTerrain,
            @RequestParam(required = false) Long societeId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Modèles compatibles avec le terrain",
                modelMaisonService.findCompatibles(surfaceTerrain, typeTerrain, societeId)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ModelMaisonResponse>>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Résultats de recherche", modelMaisonService.searchByLibeller(keyword)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ModelMaisonResponse>> update(@PathVariable Long id, @Valid @RequestBody ModelMaisonRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Modèle mis à jour avec succès", modelMaisonService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        modelMaisonService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Modèle supprimé avec succès", null));
    }

    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ModelMaisonResponse>> uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Image du modèle ajoutée avec succès", modelMaisonService.uploadImage(id, file)));
    }

    @PostMapping(value = "/{id}/plan", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ModelMaisonResponse>> uploadPlan(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Plan de maison ajouté avec succès", modelMaisonService.uploadPlan(id, file)));
    }
}
