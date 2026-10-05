package com.example.foncierback.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaUpdateRequest {

    @Size(max = 150, message = "Le titre ne doit pas dépasser 150 caractères")
    private String titre;

    private String description;
}
