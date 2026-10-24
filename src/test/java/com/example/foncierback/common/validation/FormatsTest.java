package com.example.foncierback.common.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FormatsTest {

    @Test
    void titreFoncierRemplaceLePrefixeSaisiParTF() {
        assertEquals("TF-BKO-10294", Formats.titreFoncier("TF-BKO-10294"));
        assertEquals("TF-1024 / Kati", Formats.titreFoncier("  tf 1024 / Kati "));
        assertEquals("TF-1024", Formats.titreFoncier("T.F. n°1024"));
        assertEquals("TF-12123/b", Formats.titreFoncier("TF 12123/b"));
        assertEquals("TF-r231", Formats.titreFoncier("Tf r231"));
        assertEquals("TF-12", Formats.titreFoncier("TF12"));
        assertEquals("TF-766955", Formats.titreFoncier("766955"));
        assertNull(Formats.titreFoncier("   "));
    }

    @Test
    void titreFoncierExigeUneReference() {
        assertTrue("TF-1024".matches(Formats.TITRE_FONCIER));
        assertFalse(Formats.titreFoncier("TF").matches(Formats.TITRE_FONCIER));
        assertFalse("1024".matches(Formats.TITRE_FONCIER));
    }
}
