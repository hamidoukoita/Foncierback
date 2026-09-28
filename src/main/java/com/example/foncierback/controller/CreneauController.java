package com.example.foncierback.controller;

import com.example.foncierback.common.response.ApiResponse;
import com.example.foncierback.dto.request.CreneauRequest;
import com.example.foncierback.dto.response.CreneauResponse;
import com.example.foncierback.service.CreneauService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/creneaux")
@RequiredArgsConstructor
@Tag(name = "Créneaux", description = "Gestion des créneaux de rendez-vous pour les sociétés promotrices")
public class CreneauController {

    private final CreneauService creneauService;

    @PostMapping
    @Operation(summary = "Créer un nouveau créneau de rendez-vous")
    public ResponseEntity<ApiResponse<CreneauResponse>> create(@Valid @RequestBody CreneauRequest request) {
        CreneauResponse response = creneauService.create(request);
        return new ResponseEntity<>(new ApiResponse<>(true, "Créneau créé avec succès", response), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un créneau par son ID")
    public ResponseEntity<ApiResponse<CreneauResponse>> getById(@PathVariable Long id) {
        CreneauResponse response = creneauService.getById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Créneau récupéré avec succès", response));
    }

    @GetMapping
    @Operation(summary = "Lister tous les créneaux ou filtrer par société et disponibilité")
    public ResponseEntity<ApiResponse<List<CreneauResponse>>> getAll(
            @RequestParam(required = false) Long societeId,
            @RequestParam(required = false) Boolean disponible
    ) {
        List<CreneauResponse> list;
        if (societeId != null && disponible != null) {
            list = creneauService.getBySocieteIdAndDisponible(societeId, disponible);
        } else if (societeId != null) {
            list = creneauService.getBySocieteId(societeId);
        } else {
            list = creneauService.getAll();
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des créneaux", list));
    }

    @GetMapping("/societe/{societeId}")
    @Operation(summary = "Lister les créneaux d'une société promotrice")
    public ResponseEntity<ApiResponse<List<CreneauResponse>>> getBySociete(@PathVariable Long societeId) {
        List<CreneauResponse> list = creneauService.getBySocieteId(societeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des créneaux pour la société", list));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un créneau existant")
    public ResponseEntity<ApiResponse<CreneauResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody CreneauRequest request
    ) {
        CreneauResponse response = creneauService.update(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Créneau mis à jour avec succès", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un créneau")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        creneauService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Créneau supprimé avec succès", null));
    }
}
