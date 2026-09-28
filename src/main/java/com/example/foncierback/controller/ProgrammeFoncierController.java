package com.example.foncierback.controller;

import com.example.foncierback.common.response.ApiResponse;
import com.example.foncierback.dto.request.ProgrammeFoncierRequest;
import com.example.foncierback.dto.response.ProgrammeFoncierResponse;
import com.example.foncierback.entity.enums.StatutProgramme;
import com.example.foncierback.service.ProgrammeFoncierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/programmes-fonciers")
@RequiredArgsConstructor
@Tag(name = "Programmes Fonciers", description = "Gestion des programmes fonciers et lotissements")
public class ProgrammeFoncierController {

    private final ProgrammeFoncierService programmeFoncierService;

    @PostMapping
    @Operation(summary = "Créer un nouveau programme foncier")
    public ResponseEntity<ApiResponse<ProgrammeFoncierResponse>> create(@Valid @RequestBody ProgrammeFoncierRequest request) {
        ProgrammeFoncierResponse response = programmeFoncierService.create(request);
        return new ResponseEntity<>(new ApiResponse<>(true, "Programme foncier créé avec succès", response), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un programme foncier par son identifiant")
    public ResponseEntity<ApiResponse<ProgrammeFoncierResponse>> getById(@PathVariable Long id) {
        ProgrammeFoncierResponse response = programmeFoncierService.getById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Programme foncier récupéré avec succès", response));
    }

    @GetMapping
    @Operation(summary = "Lister tous les programmes fonciers")
    public ResponseEntity<ApiResponse<List<ProgrammeFoncierResponse>>> getAll() {
        List<ProgrammeFoncierResponse> list = programmeFoncierService.getAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des programmes fonciers", list));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un programme foncier existant")
    public ResponseEntity<ApiResponse<ProgrammeFoncierResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ProgrammeFoncierRequest request
    ) {
        ProgrammeFoncierResponse response = programmeFoncierService.update(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Programme foncier mis à jour avec succès", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un programme foncier")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        programmeFoncierService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Programme foncier supprimé avec succès", null));
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des programmes fonciers par mot-clé (nom ou lieu)")
    public ResponseEntity<ApiResponse<List<ProgrammeFoncierResponse>>> search(@RequestParam String keyword) {
        List<ProgrammeFoncierResponse> list = programmeFoncierService.search(keyword);
        return ResponseEntity.ok(new ApiResponse<>(true, "Résultats de recherche des programmes fonciers", list));
    }

    @GetMapping("/statut/{statut}")
    @Operation(summary = "Filtrer les programmes fonciers par statut")
    public ResponseEntity<ApiResponse<List<ProgrammeFoncierResponse>>> filterByStatut(@PathVariable StatutProgramme statut) {
        List<ProgrammeFoncierResponse> list = programmeFoncierService.filterByStatut(statut);
        return ResponseEntity.ok(new ApiResponse<>(true, "Programmes fonciers filtrés par statut", list));
    }

    @GetMapping("/societe/{societeId}")
    @Operation(summary = "Lister les programmes fonciers d'une société promotrice")
    public ResponseEntity<ApiResponse<List<ProgrammeFoncierResponse>>> findBySociete(@PathVariable Long societeId) {
        List<ProgrammeFoncierResponse> list = programmeFoncierService.findBySociete(societeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Programmes fonciers de la société promotrice", list));
    }
}
