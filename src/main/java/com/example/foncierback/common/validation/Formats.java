package com.example.foncierback.common.validation;

/**
 * Formats de saisie partagés par les requêtes (inscriptions, comptes, agents).
 * Les champs sont nettoyés avant validation : les espaces autour sont retirés,
 * et les numéros de téléphone sont ramenés à leurs seuls chiffres.
 */
public final class Formats {

    private Formats() {
    }

    /** Nom ou prénom : lettres (accents compris), espaces, tirets et apostrophes ; commence par une lettre. */
    public static final String NOM_PERSONNE = "^\\p{L}[\\p{L} '’\\-]*$";
    public static final String MESSAGE_NOM = "ne doit contenir que des lettres";

    /** Numéro malien : 8 chiffres (mobiles 5 à 9, fixes 2), sans l'indicatif +223. */
    public static final String TELEPHONE_MALI = "^[25-9]\\d{7}$";
    public static final String MESSAGE_TELEPHONE = "Le numéro doit être un numéro malien à 8 chiffres (ex. 76 12 34 56)";

    /** Texte qui contient au moins une lettre ou un chiffre (refuse « … », « --- », etc.). */
    public static final String TEXTE_UTILE = "(?s)^.*[\\p{L}\\p{N}].*$";

    public static final int MOT_DE_PASSE_MIN = 6;

    /** Retire les espaces autour ; une saisie vide devient null (et sera refusée par @NotBlank). */
    public static String nettoyer(String valeur) {
        if (valeur == null) return null;
        String v = valeur.strip().replaceAll("\\s{2,}", " ");
        return v.isEmpty() ? null : v;
    }

    /** « +223 76 12-34.56 » → « 76123456 » : seuls les chiffres, sans l'indicatif du Mali. */
    public static String telephone(String valeur) {
        if (valeur == null) return null;
        String chiffres = valeur.replaceAll("\\D", "");
        if (chiffres.length() == 11 && chiffres.startsWith("223")) chiffres = chiffres.substring(3);
        return chiffres.isEmpty() ? null : chiffres;
    }
}
