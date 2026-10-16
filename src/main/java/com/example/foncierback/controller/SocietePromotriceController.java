package com.example.foncierback.controller;

import com.example.foncierback.common.response.ApiResponse;
import com.example.foncierback.dto.request.ReglesReservationRequest;
import com.example.foncierback.dto.request.SocietePromotriceRequest;
import com.example.foncierback.dto.response.SocietePromotriceResponse;
import com.example.foncierback.entity.enums.StatutAgrement;
import com.example.foncierback.service.SocietePromotriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/societes-promotrices")
@RequiredArgsConstructor
@Tag(name = "Sociétés Promotrices", description = "Gestion des sociétés promotrices immobilières et foncières")
public class SocietePromotriceController {

    private final SocietePromotriceService societePromotriceService;

    @PostMapping
    @Operation(summary = "Créer une nouvelle société promotrice")
    public ResponseEntity<ApiResponse<SocietePromotriceResponse>> create(@Valid @RequestBody SocietePromotriceRequest request) {
        SocietePromotriceResponse response = societePromotriceService.create(request);
        return new ResponseEntity<>(new ApiResponse<>(true, "Société promotrice créée avec succès", response), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une société promotrice par son ID")
    public ResponseEntity<ApiResponse<SocietePromotriceResponse>> getById(@PathVariable Long id) {
        SocietePromotriceResponse response = societePromotriceService.getById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Société promotrice récupérée avec succès", response));
    }

    @GetMapping
    @Operation(summary = "Lister toutes les sociétés promotrices ou filtrer par statut d'agrément")
    public ResponseEntity<ApiResponse<List<SocietePromotriceResponse>>> getAll(
            @RequestParam(required = false) StatutAgrement statut
    ) {
        List<SocietePromotriceResponse> list = statut != null
                ? societePromotriceService.getByStatutAgrement(statut)
                : societePromotriceService.getAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des sociétés promotrices", list));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('AGENT') and hasAuthority('RESPONSABLE_SOCIETE') and @securityUtils.belongsToSocieteOrAdmin(#id))")
    @Operation(summary = "Modifier une société promotrice existante")
    public ResponseEntity<ApiResponse<SocietePromotriceResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody SocietePromotriceRequest request
    ) {
        SocietePromotriceResponse response = societePromotriceService.update(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Société promotrice mise à jour avec succès", response));
    }

    @PatchMapping("/{id}/regles-reservation")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('AGENT') and hasAuthority('RESPONSABLE_SOCIETE') and @securityUtils.belongsToSocieteOrAdmin(#id))")
    @Operation(summary = "Fixer les règles de réservation de la société (délai d'attribution, réservation multiple)")
    public ResponseEntity<ApiResponse<SocietePromotriceResponse>> updateReglesReservation(
            @PathVariable Long id,
            @Valid @RequestBody ReglesReservationRequest request
    ) {
        SocietePromotriceResponse response = societePromotriceService.updateReglesReservation(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Règles de réservation mises à jour", response));
    }

    @PostMapping(value = "/{id}/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') or (hasRole('AGENT') and hasAuthority('RESPONSABLE_SOCIETE') and @securityUtils.belongsToSocieteOrAdmin(#id))")
    @Operation(summary = "Envoyer le logo de la société (JPEG, PNG ou WebP, 2 Mo maximum)")
    public ResponseEntity<ApiResponse<SocietePromotriceResponse>> updateLogo(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile fichier
    ) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Logo mis à jour", societePromotriceService.updateLogo(id, fichier)));
    }

    @PatchMapping("/{id}/parametrage-initial")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('AGENT') and hasAuthority('RESPONSABLE_SOCIETE') and @securityUtils.belongsToSocieteOrAdmin(#id))")
    @Operation(summary = "Terminer (ou passer) l'assistant de première connexion de la société")
    public ResponseEntity<ApiResponse<SocietePromotriceResponse>> terminerParametrageInitial(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Paramétrage initial terminé",
                societePromotriceService.terminerParametrageInitial(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer une société promotrice")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        societePromotriceService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Société promotrice supprimée avec succès", null));
    }
}
