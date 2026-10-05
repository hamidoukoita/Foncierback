package com.example.foncierback.controller;

import com.example.foncierback.common.response.ApiResponse;
import com.example.foncierback.dto.request.ReservationRequest;
import com.example.foncierback.dto.response.ReservationResponse;
import com.example.foncierback.entity.enums.StatutReservation;
import com.example.foncierback.service.ReservationService;
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
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Réservations", description = "Gestion des réservations de biens fonciers")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ACQUEREUR')")
    @Operation(summary = "Créer une nouvelle réservation")
    public ResponseEntity<ApiResponse<ReservationResponse>> create(@Valid @RequestBody ReservationRequest request) {
        ReservationResponse response = reservationService.create(request);
        return new ResponseEntity<>(new ApiResponse<>(true, "Réservation créée avec succès", response), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'ACQUEREUR')")
    @Operation(summary = "Récupérer une réservation par ID")
    public ResponseEntity<ApiResponse<ReservationResponse>> getById(@PathVariable Long id) {
        ReservationResponse response = reservationService.getById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Réservation récupérée avec succès", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Operation(summary = "Lister toutes les réservations")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getAll() {
        List<ReservationResponse> list = reservationService.getAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des réservations", list));
    }

    @GetMapping("/acquereur/{acquereurId}")
    @PreAuthorize("hasRole('ADMIN') or @securityUtils.isOwnerOrAdmin(#acquereurId)")
    @Operation(summary = "Lister les réservations d'un acquéreur")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getByAcquereur(@PathVariable Long acquereurId) {
        List<ReservationResponse> list = reservationService.findByAcquereur(acquereurId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Réservations de l'acquéreur", list));
    }

    @GetMapping("/bien/{bienId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Operation(summary = "Lister les réservations associées à un bien foncier")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getByBien(@PathVariable Long bienId) {
        List<ReservationResponse> list = reservationService.findByBien(bienId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Réservations du bien", list));
    }

    @GetMapping("/societe/{societeId}")
    @PreAuthorize("hasRole('ADMIN') or @securityUtils.belongsToSocieteOrAdmin(#societeId)")
    @Operation(summary = "Lister les réservations d'une société promotrice")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getBySociete(@PathVariable Long societeId) {
        List<ReservationResponse> list = reservationService.findBySociete(societeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Réservations de la société promotrice", list));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Operation(summary = "Modifier une réservation existante")
    public ResponseEntity<ApiResponse<ReservationResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ReservationRequest request
    ) {
        ReservationResponse response = reservationService.update(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Réservation mise à jour avec succès", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer une réservation")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        reservationService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Réservation supprimée avec succès", null));
    }

    @PatchMapping("/{id}/confirmer")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Operation(summary = "Confirmer une réservation")
    public ResponseEntity<ApiResponse<ReservationResponse>> confirmer(@PathVariable Long id) {
        ReservationResponse response = reservationService.confirmer(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Réservation confirmée avec succès", response));
    }

    @PatchMapping("/{id}/refuser")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Operation(summary = "Refuser une réservation avec motif")
    public ResponseEntity<ApiResponse<ReservationResponse>> refuser(
            @PathVariable Long id,
            @RequestParam String motif
    ) {
        ReservationResponse response = reservationService.refuser(id, motif);
        return ResponseEntity.ok(new ApiResponse<>(true, "Réservation refusée", response));
    }

    @PatchMapping("/{id}/statut")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Operation(summary = "Changer le statut d'une réservation")
    public ResponseEntity<ApiResponse<ReservationResponse>> changerStatut(
            @PathVariable Long id,
            @RequestParam StatutReservation statut,
            @RequestParam(required = false) String motifRefus
    ) {
        ReservationResponse response = reservationService.changerStatut(id, statut, motifRefus);
        return ResponseEntity.ok(new ApiResponse<>(true, "Statut de réservation mis à jour", response));
    }
}
