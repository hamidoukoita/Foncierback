package com.example.foncierback.controller;

import com.example.foncierback.common.response.ApiResponse;
import com.example.foncierback.dto.request.PlanMasseElementRequest;
import com.example.foncierback.dto.response.PlanMasseElementResponse;
import com.example.foncierback.service.PlanMasseElementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans-masse-elements")
@RequiredArgsConstructor
@Tag(name = "Éléments du plan de masse", description = "Routes, espaces verts et points d'intérêt positionnables sur un plan de masse")
public class PlanMasseElementController {

    private final PlanMasseElementService service;

    @PostMapping
    @Operation(summary = "Ajouter un élément au plan de masse")
    public ResponseEntity<ApiResponse<PlanMasseElementResponse>> create(@Valid @RequestBody PlanMasseElementRequest request) {
        return new ResponseEntity<>(new ApiResponse<>(true, "Élément du plan ajouté avec succès", service.create(request)), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un élément du plan")
    public ResponseEntity<ApiResponse<PlanMasseElementResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Élément récupéré avec succès", service.getById(id)));
    }

    @GetMapping("/plan-masse/{planMasseId}")
    @Operation(summary = "Lister les éléments d'un plan de masse")
    public ResponseEntity<ApiResponse<List<PlanMasseElementResponse>>> getByPlanMasseId(@PathVariable Long planMasseId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Éléments du plan de masse", service.getByPlanMasseId(planMasseId)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un élément du plan")
    public ResponseEntity<ApiResponse<PlanMasseElementResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody PlanMasseElementRequest request
    ) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Élément du plan mis à jour avec succès", service.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un élément du plan")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Élément du plan supprimé avec succès", null));
    }
}
