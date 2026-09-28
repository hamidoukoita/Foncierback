package com.example.foncierback.controller;

import com.example.foncierback.common.response.ApiResponse;
import com.example.foncierback.dto.request.PlanMasseRequest;
import com.example.foncierback.dto.response.PlanMasseResponse;
import com.example.foncierback.service.PlanMasseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans-masse")
@RequiredArgsConstructor
@Tag(name = "Plans de Masse", description = "Gestion des plans de masse associés aux programmes fonciers")
public class PlanMasseController {

    private final PlanMasseService planMasseService;

    @PostMapping
    @Operation(summary = "Créer un nouveau plan de masse")
    public ResponseEntity<ApiResponse<PlanMasseResponse>> create(@Valid @RequestBody PlanMasseRequest request) {
        PlanMasseResponse response = planMasseService.create(request);
        return new ResponseEntity<>(new ApiResponse<>(true, "Plan de masse créé avec succès", response), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un plan de masse par son ID")
    public ResponseEntity<ApiResponse<PlanMasseResponse>> getById(@PathVariable Long id) {
        PlanMasseResponse response = planMasseService.getById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Plan de masse récupéré avec succès", response));
    }

    @GetMapping
    @Operation(summary = "Lister tous les plans de masse")
    public ResponseEntity<ApiResponse<List<PlanMasseResponse>>> getAll() {
        List<PlanMasseResponse> list = planMasseService.getAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des plans de masse", list));
    }

    @GetMapping("/programme/{programmeId}")
    @Operation(summary = "Récupérer le plan de masse d'un programme foncier")
    public ResponseEntity<ApiResponse<PlanMasseResponse>> getByProgrammeId(@PathVariable Long programmeId) {
        PlanMasseResponse response = planMasseService.getByProgrammeId(programmeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Plan de masse du programme récupéré avec succès", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un plan de masse existant")
    public ResponseEntity<ApiResponse<PlanMasseResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody PlanMasseRequest request
    ) {
        PlanMasseResponse response = planMasseService.update(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Plan de masse mis à jour avec succès", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un plan de masse")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        planMasseService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Plan de masse supprimé avec succès", null));
    }
}
