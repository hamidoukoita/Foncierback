package com.example.foncierback.entity.enums;

/** Nature d'un média rattaché à un programme ou à un bien foncier. */
public enum TypeMedia {
    PHOTO,
    PANORAMA_360,
    /**
     * Ancien lien externe (Matterport, Kuula…) : plus proposé ni renvoyé par l'API.
     * Conservé pour que les lignes déjà en base restent lisibles.
     */
    @Deprecated
    VISITE_VIRTUELLE
}
