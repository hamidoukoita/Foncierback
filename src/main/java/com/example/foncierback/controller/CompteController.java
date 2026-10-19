package com.example.foncierback.controller;

import com.example.foncierback.common.response.ApiResponse;
import com.example.foncierback.dto.request.ChangementMotDePasseRequest;
import com.example.foncierback.dto.request.ChangementTelephoneRequest;
import com.example.foncierback.dto.request.DemandeCodeRequest;
import com.example.foncierback.dto.request.ReinitialisationMotDePasseRequest;
import com.example.foncierback.dto.response.AuthResponse;
import com.example.foncierback.dto.response.CodeSimuleResponse;
import com.example.foncierback.service.CompteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Compte", description = "Numéro de connexion et mot de passe de l'utilisateur")
public class CompteController {

    private final CompteService compteService;

    @PatchMapping("/api/comptes/moi/telephone")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Changer son numéro de connexion (confirmé par le mot de passe) ; renvoie une nouvelle session")
    public ResponseEntity<ApiResponse<AuthResponse>> changerTelephone(@Valid @RequestBody ChangementTelephoneRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Numéro de téléphone mis à jour", compteService.changerTelephone(request)));
    }

    @PatchMapping("/api/comptes/moi/mot-de-passe")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Changer son mot de passe")
    public ResponseEntity<ApiResponse<Void>> changerMotDePasse(@Valid @RequestBody ChangementMotDePasseRequest request) {
        compteService.changerMotDePasse(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Mot de passe mis à jour", null));
    }

    @PostMapping("/api/auth/mot-de-passe-oublie")
    @Operation(summary = "Mot de passe oublié : obtenir un code (SMS simulé, renvoyé dans la réponse)")
    public ResponseEntity<ApiResponse<CodeSimuleResponse>> demanderCode(@Valid @RequestBody DemandeCodeRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Code envoyé", compteService.demanderCodeReinitialisation(request.getTelephone())));
    }

    @PostMapping("/api/auth/mot-de-passe-oublie/confirmer")
    @Operation(summary = "Mot de passe oublié : choisir un nouveau mot de passe avec le code reçu")
    public ResponseEntity<ApiResponse<Void>> reinitialiser(@Valid @RequestBody ReinitialisationMotDePasseRequest request) {
        compteService.reinitialiserMotDePasse(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Mot de passe réinitialisé : vous pouvez vous connecter", null));
    }
}
