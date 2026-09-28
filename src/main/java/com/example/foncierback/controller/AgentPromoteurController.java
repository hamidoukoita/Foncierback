package com.example.foncierback.controller;

import com.example.foncierback.common.response.ApiResponse;
import com.example.foncierback.dto.request.AgentPromoteurRequest;
import com.example.foncierback.dto.response.AgentPromoteurResponse;
import com.example.foncierback.service.AgentPromoteurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agents-promoteurs")
@RequiredArgsConstructor
@Tag(name = "Agents Promoteurs", description = "Gestion des profils agents promoteurs et responsables")
public class AgentPromoteurController {

    private final AgentPromoteurService agentPromoteurService;

    @PostMapping
    @Operation(summary = "Créer un nouvel agent promoteur")
    public ResponseEntity<ApiResponse<AgentPromoteurResponse>> create(@Valid @RequestBody AgentPromoteurRequest request) {
        AgentPromoteurResponse response = agentPromoteurService.create(request);
        return new ResponseEntity<>(new ApiResponse<>(true, "Agent promoteur créé avec succès", response), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un agent promoteur par son ID")
    public ResponseEntity<ApiResponse<AgentPromoteurResponse>> getById(@PathVariable Long id) {
        AgentPromoteurResponse response = agentPromoteurService.getById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Agent promoteur récupéré avec succès", response));
    }

    @GetMapping
    @Operation(summary = "Lister tous les agents promoteurs")
    public ResponseEntity<ApiResponse<List<AgentPromoteurResponse>>> getAll() {
        List<AgentPromoteurResponse> list = agentPromoteurService.getAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des agents promoteurs", list));
    }

    @GetMapping("/societe/{societeId}")
    @Operation(summary = "Lister les agents promoteurs d'une société")
    public ResponseEntity<ApiResponse<List<AgentPromoteurResponse>>> getBySocieteId(@PathVariable Long societeId) {
        List<AgentPromoteurResponse> list = agentPromoteurService.getBySocieteId(societeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Agents promoteurs de la société récupérés avec succès", list));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un agent promoteur existant")
    public ResponseEntity<ApiResponse<AgentPromoteurResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody AgentPromoteurRequest request
    ) {
        AgentPromoteurResponse response = agentPromoteurService.update(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Agent promoteur mis à jour avec succès", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un agent promoteur")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        agentPromoteurService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Agent promoteur supprimé avec succès", null));
    }
}
