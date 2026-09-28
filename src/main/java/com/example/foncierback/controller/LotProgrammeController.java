package com.example.foncierback.controller;

import com.example.foncierback.common.response.ApiResponse;
import com.example.foncierback.dto.request.LotProgrammeRequest;
import com.example.foncierback.dto.response.LotProgrammeResponse;
import com.example.foncierback.service.LotProgrammeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lots-programmes")
@RequiredArgsConstructor
@Tag(name = "Lots de Programmes", description = "Gestion des lots au sein des programmes fonciers")
public class LotProgrammeController {

    private final LotProgrammeService lotProgrammeService;

    @PostMapping
    @Operation(summary = "Créer un nouveau lot de programme foncier")
    public ResponseEntity<ApiResponse<LotProgrammeResponse>> create(@Valid @RequestBody LotProgrammeRequest request) {
        LotProgrammeResponse response = lotProgrammeService.create(request);
        return new ResponseEntity<>(new ApiResponse<>(true, "Lot de programme créé avec succès", response), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un lot de programme par son identifiant")
    public ResponseEntity<ApiResponse<LotProgrammeResponse>> getById(@PathVariable Long id) {
        LotProgrammeResponse response = lotProgrammeService.getById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lot de programme récupéré avec succès", response));
    }

    @GetMapping
    @Operation(summary = "Lister tous les lots de programmes")
    public ResponseEntity<ApiResponse<List<LotProgrammeResponse>>> getAll() {
        List<LotProgrammeResponse> list = lotProgrammeService.getAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des lots de programmes", list));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un lot de programme existant")
    public ResponseEntity<ApiResponse<LotProgrammeResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody LotProgrammeRequest request
    ) {
        LotProgrammeResponse response = lotProgrammeService.update(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lot de programme mis à jour avec succès", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un lot de programme")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        lotProgrammeService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lot de programme supprimé avec succès", null));
    }

    @GetMapping("/programme/{programmeId}")
    @Operation(summary = "Lister les lots d'un programme foncier donné")
    public ResponseEntity<ApiResponse<List<LotProgrammeResponse>>> getByProgrammeId(@PathVariable Long programmeId) {
        List<LotProgrammeResponse> list = lotProgrammeService.getByProgrammeId(programmeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lots du programme foncier", list));
    }
}
