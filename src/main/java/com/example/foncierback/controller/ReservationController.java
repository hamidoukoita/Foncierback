package com.example.foncierback.controller;

import com.example.foncierback.common.response.ApiResponse;
import com.example.foncierback.dto.request.ReservationRequest;
import com.example.foncierback.dto.response.ReservationResponse;
import com.example.foncierback.service.ReservationService;
import com.example.foncierback.service.SuiteVenteService;
import com.example.foncierback.dto.response.BienRemplacementResponse;
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
    private final SuiteVenteService suiteVenteService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ACQUEREUR')")
    @Operation(summary = "Créer une nouvelle réservation")
    public ResponseEntity<ApiResponse<ReservationResponse>> create(@Valid @RequestBody ReservationRequest request) {
        ReservationResponse response = reservationService.create(request);
        return new ResponseEntity<>(new ApiResponse<>(true, "Réservation créée avec succès", response), HttpStatus.CREATED);
    }

    @GetMapping("/conditions/{bienId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Montant, durée et disponibilité d'un bien avant de le réserver")
    public ResponseEntity<ApiResponse<com.example.foncierback.dto.response.ConditionsReservationResponse>> conditions(
            @PathVariable Long bienId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Conditions de réservation", reservationService.conditions(bienId)));
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

    @GetMapping("/agent/{agentId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('AGENT') and @securityUtils.isOwnerOrAdmin(#agentId))")
    @Operation(summary = "Lister les réservations assignées à un agent promoteur")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getByAgent(@PathVariable Long agentId) {
        List<ReservationResponse> list = reservationService.findByAgent(agentId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Réservations de l'agent", list));
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

    @PatchMapping("/{id}/agent/{agentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Operation(summary = "Attribuer une demande de réservation à un agent actif de la société")
    public ResponseEntity<ApiResponse<ReservationResponse>> assignerAgent(
            @PathVariable Long id,
            @PathVariable Long agentId
    ) {
        ReservationResponse response = reservationService.assignerAgent(id, agentId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Agent attribué à la réservation", response));
    }

    @PatchMapping("/{id}/demarrer")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Operation(summary = "Démarrer le décompte de la réservation (agent chargé du dossier)")
    public ResponseEntity<ApiResponse<ReservationResponse>> demarrer(@PathVariable Long id) {
        ReservationResponse response = reservationService.demarrerDecompte(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Décompte de la réservation démarré", response));
    }

    @PatchMapping("/{id}/vendre")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Operation(summary = "Conclure la vente : le bien passe vendu, les autres réservations sont remboursées")
    public ResponseEntity<ApiResponse<ReservationResponse>> vendre(@PathVariable Long id) {
        ReservationResponse response = reservationService.conclureVente(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Vente conclue", response));
    }

    @GetMapping("/{id}/biens-remplacement")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACQUEREUR')")
    @Operation(summary = "Biens disponibles de la même société pour reporter une réservation (bien vendu à un autre)")
    public ResponseEntity<ApiResponse<List<BienRemplacementResponse>>> biensRemplacement(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Biens de remplacement récupérés", suiteVenteService.biensDeRemplacement(id)));
    }

    @PatchMapping("/{id}/remboursement")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACQUEREUR')")
    @Operation(summary = "Demander le remboursement (sous 24 h) d'une réservation dont le bien a été vendu à un autre")
    public ResponseEntity<ApiResponse<ReservationResponse>> rembourser(@PathVariable Long id) {
        ReservationResponse response = suiteVenteService.demanderRemboursement(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Remboursement demandé", response));
    }

    @PatchMapping("/{id}/remplacement/{bienId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACQUEREUR')")
    @Operation(summary = "Reporter le montant payé sur un autre bien de la société, sans nouveau paiement")
    public ResponseEntity<ApiResponse<ReservationResponse>> remplacer(@PathVariable Long id, @PathVariable Long bienId) {
        ReservationResponse response = suiteVenteService.remplacer(id, bienId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Réservation reportée sur le nouveau bien", response));
    }

    @PatchMapping("/{id}/annuler")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACQUEREUR')")
    @Operation(summary = "Annuler sa réservation (acquéreur) : remboursée selon le pourcentage fixé par la société")
    public ResponseEntity<ApiResponse<ReservationResponse>> annuler(@PathVariable Long id) {
        ReservationResponse response = reservationService.annulerParAcquereur(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Réservation annulée", response));
    }
}
