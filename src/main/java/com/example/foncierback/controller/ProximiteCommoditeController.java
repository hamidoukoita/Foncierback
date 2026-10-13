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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proximites-commodites")
@RequiredArgsConstructor
@Tag(name = "Proximités commodités", description = "Rattachement commodité ↔ programme / parcelle")
public class ProximiteCommoditeController {

    private final ProximiteCommoditeService proximiteCommoditeService;

    @PostMapping
    @Operation(summary = "Créer un rattachement")
    public ResponseEntity<ApiResponse<ProximiteCommoditeResponse>> create(
            @Valid @RequestBody ProximiteCommoditeRequest request
    ) {
        ProximiteCommoditeResponse response = proximiteCommoditeService.create(request);
        return new ResponseEntity<>(new ApiResponse<>(true, "Rattachement créé", response), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un rattachement")
    public ResponseEntity<ApiResponse<ProximiteCommoditeResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Rattachement", proximiteCommoditeService.getById(id)));
    }

    @GetMapping("/societe/{societeId}")
    @Operation(summary = "Lister les rattachements d'une société")
    public ResponseEntity<ApiResponse<List<ProximiteCommoditeResponse>>> getBySociete(
            @PathVariable Long societeId
    ) {
        List<ProximiteCommoditeResponse> list = proximiteCommoditeService.getBySocieteId(societeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Rattachements de la société", list));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un rattachement")
    public ResponseEntity<ApiResponse<ProximiteCommoditeResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ProximiteCommoditeRequest request
    ) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Rattachement mis à jour",
                proximiteCommoditeService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un rattachement")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        proximiteCommoditeService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Rattachement supprimé", null));
    }
}
