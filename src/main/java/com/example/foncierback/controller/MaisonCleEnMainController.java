package com.example.foncierback.controller;

import com.example.foncierback.common.response.ApiResponse;
import com.example.foncierback.dto.request.MaisonCleEnMainRequest;
import com.example.foncierback.dto.response.MaisonCleEnMainResponse;
import com.example.foncierback.service.MaisonCleEnMainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maisons-cle-en-main")
@RequiredArgsConstructor
@Tag(name = "Maisons clés en main", description = "Maisons prêtes à habiter publiées par les sociétés promotrices")
public class MaisonCleEnMainController {

    private final MaisonCleEnMainService maisonService;

    @GetMapping
    @Operation(summary = "Catalogue public des maisons clés en main (sociétés agréées)")
    public ResponseEntity<ApiResponse<List<MaisonCleEnMainResponse>>> catalogue() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Maisons clés en main", maisonService.getCatalogue()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'une maison clé en main")
    public ResponseEntity<ApiResponse<MaisonCleEnMainResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Maison clé en main récupérée", maisonService.getById(id)));
    }

    @GetMapping("/societe/{societeId}")
    @Operation(summary = "Maisons clés en main d'une société (publiques et brouillons de gestion)")
    public ResponseEntity<ApiResponse<List<MaisonCleEnMainResponse>>> parSociete(@PathVariable Long societeId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Maisons clés en main de la société", maisonService.getBySociete(societeId)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or (hasRole('AGENT') and hasAuthority('RESPONSABLE_SOCIETE'))")
    @Operation(summary = "Publier une maison clé en main")
    public ResponseEntity<ApiResponse<MaisonCleEnMainResponse>> create(@Valid @RequestBody MaisonCleEnMainRequest request) {
        return new ResponseEntity<>(new ApiResponse<>(true, "Maison clé en main publiée", maisonService.create(request)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('AGENT') and hasAuthority('RESPONSABLE_SOCIETE'))")
    @Operation(summary = "Modifier une maison clé en main")
    public ResponseEntity<ApiResponse<MaisonCleEnMainResponse>> update(@PathVariable Long id, @Valid @RequestBody MaisonCleEnMainRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Maison clé en main mise à jour", maisonService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('AGENT') and hasAuthority('RESPONSABLE_SOCIETE'))")
    @Operation(summary = "Supprimer une maison clé en main")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        maisonService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Maison clé en main supprimée", null));
    }
}
