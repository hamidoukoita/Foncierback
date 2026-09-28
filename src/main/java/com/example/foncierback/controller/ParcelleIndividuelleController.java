package com.example.foncierback.controller;

import com.example.foncierback.common.response.ApiResponse;
import com.example.foncierback.dto.request.ParcelleIndividuelleRequest;
import com.example.foncierback.dto.response.ParcelleIndividuelleResponse;
import com.example.foncierback.service.ParcelleIndividuelleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parcelles-individuelles")
@RequiredArgsConstructor
@Tag(name = "Parcelles Individuelles", description = "Gestion des parcelles individuelles")
public class ParcelleIndividuelleController {

    private final ParcelleIndividuelleService parcelleIndividuelleService;

    @PostMapping
    @Operation(summary = "Créer une nouvelle parcelle individuelle")
    public ResponseEntity<ApiResponse<ParcelleIndividuelleResponse>> create(@Valid @RequestBody ParcelleIndividuelleRequest request) {
        ParcelleIndividuelleResponse response = parcelleIndividuelleService.create(request);
        return new ResponseEntity<>(new ApiResponse<>(true, "Parcelle individuelle créée avec succès", response), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une parcelle individuelle par son identifiant")
    public ResponseEntity<ApiResponse<ParcelleIndividuelleResponse>> getById(@PathVariable Long id) {
        ParcelleIndividuelleResponse response = parcelleIndividuelleService.getById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Parcelle individuelle récupérée avec succès", response));
    }

    @GetMapping
    @Operation(summary = "Lister toutes les parcelles individuelles")
    public ResponseEntity<ApiResponse<List<ParcelleIndividuelleResponse>>> getAll() {
        List<ParcelleIndividuelleResponse> list = parcelleIndividuelleService.getAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des parcelles individuelles", list));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une parcelle individuelle existante")
    public ResponseEntity<ApiResponse<ParcelleIndividuelleResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ParcelleIndividuelleRequest request
    ) {
        ParcelleIndividuelleResponse response = parcelleIndividuelleService.update(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Parcelle individuelle mise à jour avec succès", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une parcelle individuelle")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        parcelleIndividuelleService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Parcelle individuelle supprimée avec succès", null));
    }

    @GetMapping("/societe/{societeId}")
    @Operation(summary = "Lister les parcelles individuelles d'une société promotrice")
    public ResponseEntity<ApiResponse<List<ParcelleIndividuelleResponse>>> getBySocieteId(@PathVariable Long societeId) {
        List<ParcelleIndividuelleResponse> list = parcelleIndividuelleService.getBySocieteId(societeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Parcelles individuelles de la société promotrice", list));
    }
}
