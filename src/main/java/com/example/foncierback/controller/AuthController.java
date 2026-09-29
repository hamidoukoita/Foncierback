package com.example.foncierback.controller;

import com.example.foncierback.common.response.ApiResponse;
import com.example.foncierback.dto.request.AcquereurRequest;
import com.example.foncierback.dto.request.LoginRequest;
import com.example.foncierback.dto.response.AuthResponse;
import com.example.foncierback.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Login JWT et inscription acquéreur")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Connexion (téléphone + mot de passe)")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Connexion réussie", response));
    }

    @PostMapping("/register")
    @Operation(summary = "Inscription d'un acquéreur")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody AcquereurRequest request) {
        AuthResponse response = authService.registerAcquereur(request);
        return new ResponseEntity<>(
                new ApiResponse<>(true, "Inscription réussie", response),
                HttpStatus.CREATED
        );
    }
}