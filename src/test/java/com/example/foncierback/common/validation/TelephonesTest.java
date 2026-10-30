package com.example.foncierback.common.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TelephonesTest {

    @Test
    void sansIndicatifLeNumeroEstMalien() {
        assertEquals("+22376123456", Telephones.normaliser("76 12 34 56"));
        assertEquals("+22370000000", Telephones.normaliser("70000000"));
        assertEquals("+22320229050", Telephones.normaliser("20-22-90-50"));
    }

    @Test
    void indicatifSaisiDeToutesLesFacons() {
        assertEquals("+22376123456", Telephones.normaliser("+223 76 12 34 56"));
        assertEquals("+22376123456", Telephones.normaliser("0022376123456"));
        assertEquals("+22376123456", Telephones.normaliser("22376123456"));
        assertEquals("+33612345678", Telephones.normaliser("+33 6 12 34 56 78"));
        assertEquals("+33612345678", Telephones.normaliser("0033 6.12.34.56.78"));
        assertEquals("+12025550143", Telephones.normaliser("+1 (202) 555-0143"));
        assertEquals("+2250707070707", Telephones.normaliser("+225 07 07 07 07 07"));
    }

    @Test
    void numeroInvalideGardeSaSaisieBrutePourLaValidation() {
        assertEquals("+33612", Telephones.normaliser("+33 6 12"));
        assertFalse(Telephones.estValide("+33612"));
        assertFalse(Telephones.estValide("12345"));
        assertNull(Telephones.normaliser("   "));
        assertNull(Telephones.normaliser(null));
    }

    @Test
    void seulsLesNumerosMaliensPassentLaRegleMali() {
        assertTrue(Telephones.estMalien("+22376123456"));
        assertFalse(Telephones.estMalien("+33612345678"));
        assertTrue(Telephones.estValide("+33612345678"));
    }

    @Test
    void messageDErreurDonneLePaysEtUnExemple() {
        String message = Telephones.messageInvalide("+33612", false);
        assertTrue(message.startsWith("Numéro invalide pour France (ex. +33 "), message);
        assertTrue(Telephones.messageInvalide("+33612345678", true).contains("malien"));
        // Sans indicatif, la saisie est lue comme malienne.
        assertTrue(Telephones.messageInvalide("12", false).startsWith("Numéro invalide pour Mali"));
    }
}
