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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Réservations", description = "Gestion des réservations de biens fonciers")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @Operation(summary = "Créer une nouvelle réservation")
    public ResponseEntity<ApiResponse<ReservationResponse>> create(@Valid @RequestBody ReservationRequest request) {
        ReservationResponse response = reservationService.create(request);
        return new ResponseEntity<>(new ApiResponse<>(true, "Réservation créée avec succès", response), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une réservation par son ID")
    public ResponseEntity<ApiResponse<ReservationResponse>> getById(@PathVariable Long id) {
        ReservationResponse response = reservationService.getById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Réservation récupérée avec succès", response));
    }

    @GetMapping
    @Operation(summary = "Lister toutes les réservations")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getAll() {
        List<ReservationResponse> list = reservationService.getAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des réservations", list));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une réservation existante")
    public ResponseEntity<ApiResponse<ReservationResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ReservationRequest request
    ) {
        ReservationResponse response = reservationService.update(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Réservation mise à jour avec succès", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une réservation")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        reservationService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Réservation supprimée avec succès", null));
    }

    @PatchMapping("/{id}/confirmer")
    @Operation(summary = "Confirmer une réservation")
    public ResponseEntity<ApiResponse<ReservationResponse>> confirmer(@PathVariable Long id) {
        ReservationResponse response = reservationService.confirmer(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Réservation confirmée avec succès", response));
    }

    @PatchMapping("/{id}/refuser")
    @Operation(summary = "Refuser une réservation")
    public ResponseEntity<ApiResponse<ReservationResponse>> refuser(
            @PathVariable Long id,
            @RequestParam(required = false) String motif
    ) {
        ReservationResponse response = reservationService.refuser(id, motif);
        return ResponseEntity.ok(new ApiResponse<>(true, "Réservation refusée avec succès", response));
    }

    @PatchMapping("/{id}/statut")
    @Operation(summary = "Changer le statut d'une réservation")
    public ResponseEntity<ApiResponse<ReservationResponse>> changerStatut(
            @PathVariable Long id,
            @RequestParam StatutReservation statut,
            @RequestParam(required = false) String motifRefus
    ) {
        ReservationResponse response = reservationService.changerStatut(id, statut, motifRefus);
        return ResponseEntity.ok(new ApiResponse<>(true, "Statut de la réservation mis à jour avec succès", response));
    }

    @GetMapping("/acquereur/{acquereurId}")
    @Operation(summary = "Lister les réservations d'un acquéreur")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> findByAcquereur(@PathVariable Long acquereurId) {
        List<ReservationResponse> list = reservationService.findByAcquereur(acquereurId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Réservations de l'acquéreur", list));
    }

    @GetMapping("/bien/{bienId}")
    @Operation(summary = "Lister les réservations associées à un bien foncier")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> findByBien(@PathVariable Long bienId) {
        List<ReservationResponse> list = reservationService.findByBien(bienId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Réservations pour le bien foncier", list));
    }
}
