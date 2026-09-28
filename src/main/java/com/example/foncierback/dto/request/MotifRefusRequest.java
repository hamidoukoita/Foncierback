package com.example.foncierback.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MotifRefusRequest {

    @NotBlank(message = "Le motif de refus est obligatoire")
    private String motif;
}
