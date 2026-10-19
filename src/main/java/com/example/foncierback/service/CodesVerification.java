package com.example.foncierback.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Codes à usage unique « envoyés par SMS » (simulation : aucun opérateur n'est appelé).
 * Un code vaut pour un numéro, expire après quelques minutes et se bloque après trop d'essais.
 */
@Component
public class CodesVerification {

    public static final int VALIDITE_MINUTES = 10;
    private static final int ESSAIS_MAX = 5;
    private static final SecureRandom ALEA = new SecureRandom();

    private record Code(String valeur, LocalDateTime expiration, int essais) {
    }

    private final Map<String, Code> codes = new ConcurrentHashMap<>();

    public String generer(String telephone) {
        String valeur = String.format("%06d", ALEA.nextInt(1_000_000));
        codes.put(telephone, new Code(valeur, LocalDateTime.now().plusMinutes(VALIDITE_MINUTES), 0));
        return valeur;
    }

    /** Vrai si le code est bon (il est alors consommé). */
    public boolean verifier(String telephone, String valeur) {
        Code code = codes.get(telephone);
        if (code == null || code.expiration().isBefore(LocalDateTime.now()) || code.essais() >= ESSAIS_MAX) {
            codes.remove(telephone);
            return false;
        }
        if (code.valeur().equals(valeur)) {
            codes.remove(telephone);
            return true;
        }
        codes.put(telephone, new Code(code.valeur(), code.expiration(), code.essais() + 1));
        return false;
    }
}
