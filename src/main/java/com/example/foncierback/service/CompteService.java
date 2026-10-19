package com.example.foncierback.service;

import com.example.foncierback.dto.request.ChangementMotDePasseRequest;
import com.example.foncierback.dto.request.ChangementTelephoneRequest;
import com.example.foncierback.dto.request.ReinitialisationMotDePasseRequest;
import com.example.foncierback.dto.response.AuthResponse;
import com.example.foncierback.dto.response.CodeSimuleResponse;

/** Identifiants de connexion de l'utilisateur : numéro de téléphone et mot de passe. */
public interface CompteService {

    /** Change le numéro (identifiant de connexion) ; renvoie une nouvelle session, l'ancien jeton portant l'ancien numéro. */
    AuthResponse changerTelephone(ChangementTelephoneRequest request);

    void changerMotDePasse(ChangementMotDePasseRequest request);

    /** Mot de passe oublié : génère le code « envoyé par SMS » (simulé, renvoyé à l'écran). */
    CodeSimuleResponse demanderCodeReinitialisation(String telephone);

    void reinitialiserMotDePasse(ReinitialisationMotDePasseRequest request);
}
