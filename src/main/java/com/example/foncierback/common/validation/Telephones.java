package com.example.foncierback.common.validation;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import java.util.Locale;
import java.util.Optional;

/**
 * Numéros de téléphone de tous les pays, stockés au format international E.164 (+22376123456).
 * Sans indicatif, une saisie est lue comme un numéro malien : « 76 12 34 56 » → +22376123456.
 * Les règles de chaque pays viennent de libphonenumber (Google).
 */
public final class Telephones {

    public static final String MALI = "ML";

    private static final PhoneNumberUtil UTIL = PhoneNumberUtil.getInstance();

    private Telephones() {
    }

    /**
     * Saisie libre → E.164 si le numéro est valide pour son pays ; sinon la saisie réduite au « + »
     * et aux chiffres, que la validation {@link Telephone} refusera avec un message adapté.
     */
    public static String normaliser(String valeur) {
        if (valeur == null) return null;
        String v = valeur.strip();
        if (v.startsWith("00")) v = "+" + v.substring(2);
        // Ancienne habitude : l'indicatif malien tapé sans « + » (22376123456).
        if (v.replaceAll("\\D", "").matches("223\\d{8}") && !v.startsWith("+")) v = "+" + v.replaceAll("\\D", "");
        Optional<PhoneNumber> numero = analyser(v);
        if (numero.isPresent() && UTIL.isValidNumber(numero.get())) {
            return UTIL.format(numero.get(), PhoneNumberFormat.E164);
        }
        String brut = v.replaceAll("[^+\\d]", "");
        return brut.isEmpty() ? null : brut;
    }

    public static boolean estValide(String e164) {
        return analyser(e164).map(UTIL::isValidNumber).orElse(false);
    }

    public static boolean estMalien(String e164) {
        return analyser(e164).filter(UTIL::isValidNumber)
                .map(n -> MALI.equals(UTIL.getRegionCodeForNumber(n))).orElse(false);
    }

    /** Message d'erreur avec le pays reconnu et un exemple de son format. */
    public static String messageInvalide(String saisie, boolean maliUniquement) {
        if (maliUniquement) {
            return "Le numéro doit être un numéro malien à 8 chiffres (ex. 76 12 34 56)";
        }
        String region = analyser(saisie).map(UTIL::getRegionCodeForNumber).orElse(null);
        if (region == null && saisie != null && saisie.startsWith("+")) {
            region = analyser(saisie).map(n -> UTIL.getRegionCodeForCountryCode(n.getCountryCode())).orElse(null);
        }
        if (region == null || "ZZ".equals(region)) {
            return "Numéro de téléphone invalide : choisissez le pays puis saisissez le numéro";
        }
        PhoneNumber exemple = UTIL.getExampleNumberForType(region, PhoneNumberUtil.PhoneNumberType.MOBILE);
        String pays = new Locale("", region).getDisplayCountry(Locale.FRENCH);
        return exemple == null
                ? "Numéro invalide pour " + pays
                : "Numéro invalide pour " + pays + " (ex. " + UTIL.format(exemple, PhoneNumberFormat.INTERNATIONAL) + ")";
    }

    private static Optional<PhoneNumber> analyser(String valeur) {
        if (valeur == null || valeur.isBlank()) return Optional.empty();
        try {
            return Optional.of(UTIL.parse(valeur, MALI));
        } catch (NumberParseException e) {
            return Optional.empty();
        }
    }
}
