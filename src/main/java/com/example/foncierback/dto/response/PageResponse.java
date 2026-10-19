package com.example.foncierback.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.function.Function;

/** Page d'une liste longue : éléments de la page demandée et total pour « Voir plus ». */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {
    private List<T> elements;
    /** Numéro de page, à partir de 0. */
    private int page;
    private int taille;
    private long total;
    private boolean derniere;

    public static <E, T> PageResponse<T> depuis(org.springframework.data.domain.Page<E> page, Function<E, T> conversion) {
        return new PageResponse<>(page.getContent().stream().map(conversion).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements(), page.isLast());
    }
}
