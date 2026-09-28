package com.example.foncierback.controller;

import com.example.foncierback.common.response.ApiResponse;
import com.example.foncierback.dto.request.CommoditerRequest;
import com.example.foncierback.dto.response.CommoditerResponse;
import com.example.foncierback.service.CommoditerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commodites")
@RequiredArgsConstructor
@Tag(name = "Commodités", description = "Gestion des commodités")
public class CommoditerController {

    private final CommoditerService commoditerService;

    @PostMapping
    @Operation(summary = "Créer une nouvelle commodité")
    public ResponseEntity<ApiResponse<CommoditerResponse>> create(@Valid @RequestBody CommoditerRequest request) {
        CommoditerResponse response = commoditerService.create(request);
        return new ResponseEntity<>(new ApiResponse<>(true, "Commodité créée avec succès", response), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une commodité par son ID")
    public ResponseEntity<ApiResponse<CommoditerResponse>> getById(@PathVariable Long id) {
        CommoditerResponse response = commoditerService.getById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Commodité récupérée avec succès", response));
    }

    @GetMapping
    @Operation(summary = "Lister toutes les commodités")
    public ResponseEntity<ApiResponse<List<CommoditerResponse>>> getAll() {
        List<CommoditerResponse> list = commoditerService.getAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des commodités", list));
    }

    @GetMapping("/type/{typeCommoditeId}")
    @Operation(summary = "Lister les commodités par type de commodité")
    public ResponseEntity<ApiResponse<List<CommoditerResponse>>> getByTypeCommoditeId(@PathVariable Long typeCommoditeId) {
        List<CommoditerResponse> list = commoditerService.getByTypeCommoditeId(typeCommoditeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des commodités pour le type " + typeCommoditeId, list));
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des commodités par mot-clé dans le nom")
    public ResponseEntity<ApiResponse<List<CommoditerResponse>>> search(@RequestParam String keyword) {
        List<CommoditerResponse> list = commoditerService.searchByNom(keyword);
        return ResponseEntity.ok(new ApiResponse<>(true, "Résultats de recherche", list));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une commodité existante")
    public ResponseEntity<ApiResponse<CommoditerResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody CommoditerRequest request
    ) {
        CommoditerResponse response = commoditerService.update(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Commodité mise à jour avec succès", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une commodité")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        commoditerService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Commodité supprimée avec succès", null));
    }
}
