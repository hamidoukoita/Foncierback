package com.example.foncierback.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MotifRejetRequest {
    @NotBlank(message = "Le motif de rejet est obligatoire")
    private String motif;
}
