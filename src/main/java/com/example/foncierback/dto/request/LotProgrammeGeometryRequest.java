package com.example.foncierback.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LotProgrammeGeometryRequest {

    @NotBlank(message = "La géométrie du lot est obligatoire")
    private String geometryJson;
}
