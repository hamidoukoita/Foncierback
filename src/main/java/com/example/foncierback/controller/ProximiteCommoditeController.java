package com.example.foncierback.controller;

import com.example.foncierback.common.response.ApiResponse;
import com.example.foncierback.dto.request.ProximiteCommoditeRequest;
import com.example.foncierback.dto.response.ProximiteCommoditeResponse;
import com.example.foncierback.service.ProximiteCommoditeService;
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
@RequestMapping("/api/proximites-commodites")
@RequiredArgsConstructor
@Tag(name = "Commodités de proximité", description = "Rattachement des commodités aux cités et parcelles, avec distance")
public class ProximiteCommoditeController {

    private final ProximiteCommoditeService proximiteService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or (hasRole('AGENT') and hasAuthority('RESPONSABLE_SOCIETE'))")
    @Operation(summary = "Rattacher une commodité à une cité ou à une parcelle")
    public ResponseEntity<ApiResponse<ProximiteCommoditeResponse>> create(@Valid @RequestBody ProximiteCommoditeRequest request) {
        ProximiteCommoditeResponse response = proximiteService.create(request);
        return new ResponseEntity<>(new ApiResponse<>(true, "Commodité rattachée avec succès", response), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('AGENT') and hasAuthority('RESPONSABLE_SOCIETE'))")
    @Operation(summary = "Modifier la distance d'un rattachement")
    public ResponseEntity<ApiResponse<ProximiteCommoditeResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ProximiteCommoditeRequest request
    ) {
        ProximiteCommoditeResponse response = proximiteService.update(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Rattachement mis à jour avec succès", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('AGENT') and hasAuthority('RESPONSABLE_SOCIETE'))")
    @Operation(summary = "Supprimer un rattachement")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        proximiteService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Rattachement supprimé avec succès", null));
    }

    @GetMapping("/programme/{programmeId}")
    @Operation(summary = "Commodités de proximité d'une cité")
    public ResponseEntity<ApiResponse<List<ProximiteCommoditeResponse>>> getByProgramme(@PathVariable Long programmeId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Commodités de la cité", proximiteService.findByProgramme(programmeId)));
    }

    @GetMapping("/bien/{bienId}")
    @Operation(summary = "Commodités de proximité d'une parcelle")
    public ResponseEntity<ApiResponse<List<ProximiteCommoditeResponse>>> getByBien(@PathVariable Long bienId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Commodités du bien", proximiteService.findByBien(bienId)));
    }

    @GetMapping("/societe/{societeId}")
    @PreAuthorize("hasRole('ADMIN') or @securityUtils.belongsToSocieteOrAdmin(#societeId)")
    @Operation(summary = "Tous les rattachements d'une société")
    public ResponseEntity<ApiResponse<List<ProximiteCommoditeResponse>>> getBySociete(@PathVariable Long societeId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Rattachements de la société", proximiteService.findBySociete(societeId)));
    }
}
