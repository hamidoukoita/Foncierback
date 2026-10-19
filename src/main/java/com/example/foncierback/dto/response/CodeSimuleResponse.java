package com.example.foncierback.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/** Code « envoyé par SMS » : renvoyé à l'écran faute de passerelle SMS (simulation). */
@Data
@AllArgsConstructor
public class CodeSimuleResponse {
    private String telephone;
    private String code;
    private int validiteMinutes;
}
