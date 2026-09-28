package com.example.foncierback.controller;

import com.example.foncierback.common.response.ApiResponse;
import com.example.foncierback.dto.response.BienFoncierResponse;
import com.example.foncierback.entity.enums.StatutParcelle;
import com.example.foncierback.service.BienFoncierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/biens-fonciers")
@RequiredArgsConstructor
@Tag(name = "Biens Fonciers", description = "Consultation polymorphique des biens fonciers (lots de programmes et parcelles individuelles)")
public class BienFoncierController {

    private final BienFoncierService bienFoncierService;

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un bien foncier par son identifiant (vue polymorphique)")
    public ResponseEntity<ApiResponse<BienFoncierResponse>> getById(@PathVariable Long id) {
        BienFoncierResponse response = bienFoncierService.getById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Bien foncier récupéré avec succès", response));
    }

    @GetMapping
    @Operation(summary = "Lister tous les biens fonciers (vue polymorphique)")
    public ResponseEntity<ApiResponse<List<BienFoncierResponse>>> getAll() {
        List<BienFoncierResponse> list = bienFoncierService.getAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des biens fonciers", list));
    }

    @GetMapping("/statut/{statut}")
    @Operation(summary = "Lister les biens fonciers par statut")
    public ResponseEntity<ApiResponse<List<BienFoncierResponse>>> getByStatut(@PathVariable StatutParcelle statut) {
        List<BienFoncierResponse> list = bienFoncierService.getByStatut(statut);
        return ResponseEntity.ok(new ApiResponse<>(true, "Biens fonciers filtrés par statut", list));
    }
}
