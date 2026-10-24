package com.example.foncierback.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Élément du fil d'échange d'un projet, par ordre chronologique :
 * type MESSAGE (champ message) ou PROPOSITION (champ proposition).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElementFilResponse {

    public static final String MESSAGE = "MESSAGE";
    public static final String PROPOSITION = "PROPOSITION";

    private String type;
    private LocalDateTime date;
    private MessageProjetResponse message;
    private PropositionProjetResponse proposition;
}
