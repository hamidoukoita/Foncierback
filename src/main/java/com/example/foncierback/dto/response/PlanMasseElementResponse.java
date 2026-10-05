package com.example.foncierback.dto.response;

import com.example.foncierback.entity.enums.PlanElementType;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanMasseElementResponse {
    private Long id;
    private PlanElementType type;
    private String nom;
    private String description;
    private String geometryJson;
    private String styleJson;
    private Boolean visible;
    private Integer zIndex;
    private Long planMasseId;
}
