package com.example.foncierback.service;

import com.example.foncierback.dto.request.AcquereurRequest;
import com.example.foncierback.dto.request.LoginRequest;
import com.example.foncierback.dto.response.AuthResponse;

import com.example.foncierback.dto.request.RegisterSocieteRequest;
import com.example.foncierback.dto.response.SocietePromotriceResponse;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    /** Session (jeton et profil) d'un utilisateur déjà authentifié, par exemple après un changement de numéro. */
    AuthResponse sessionPour(com.example.foncierback.entity.Utilisateur user);

    AuthResponse registerAcquereur(AcquereurRequest request);

    SocietePromotriceResponse registerSociete(RegisterSocieteRequest request);
}