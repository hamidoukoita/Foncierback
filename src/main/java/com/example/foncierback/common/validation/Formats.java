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
    public static final int NOM_MIN = 2;
    public static final int NOM_MAX = 50;

    /** Texte qui contient au moins une lettre ou un chiffre (refuse « … », « --- », etc.). */
    public static final String TEXTE_UTILE = "(?s)^.*[\\p{L}\\p{N}].*$";

    public static final int MOT_DE_PASSE_MIN = 6;

    /** Numéro de Titre Foncier : toujours « TF- » suivi de la référence (ex. TF-1024/Kati). */
    public static final String TITRE_FONCIER = "^TF-[\\p{L}\\p{N}].*$";
    public static final String MESSAGE_TITRE_FONCIER =
            "Le numéro de Titre Foncier doit commencer par « TF- » suivi de sa référence (ex. TF-1024/Kati)";

    /** Retire les espaces autour ; une saisie vide devient null (et sera refusée par @NotBlank). */
    public static String nettoyer(String valeur) {
        if (valeur == null) return null;
        String v = valeur.strip().replaceAll("\\s{2,}", " ");
        return v.isEmpty() ? null : v;
    }

    /** « 76 12-34.56 » → « +22376123456 », « 06 12 34 56 78 » (+33) → « +33612345678 » : format E.164. */
    public static String telephone(String valeur) {
        return Telephones.normaliser(valeur);
    }

    /**
     * « tf 1024 / Kati », « T.F. n°1024 », « 1024 » → « TF-1024 / Kati », « TF-1024 », « TF-1024 » :
     * le préfixe saisi (quelle qu'en soit la forme) est remplacé par « TF- ».
     */
    public static String titreFoncier(String valeur) {
        String v = nettoyer(valeur);
        if (v == null) return null;
        String reference = v.replaceFirst("(?i)^T\\.?\\s*F\\.?(?=[\\s\\-:_n°º#.]|\\d|$)[\\s\\-:_.]*(n\\s*[°º]|#)?[\\s\\-:_.]*", "");
        return reference.isEmpty() ? "TF-" : "TF-" + reference;
    }
}
