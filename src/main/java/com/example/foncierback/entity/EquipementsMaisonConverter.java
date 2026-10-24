package com.example.foncierback.entity;

import com.example.foncierback.entity.enums.EquipementMaison;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

/** Équipements stockés dans une seule colonne : « TERRASSE,COUR,FORAGE ». */
@Converter
public class EquipementsMaisonConverter implements AttributeConverter<Set<EquipementMaison>, String> {

    @Override
    public String convertToDatabaseColumn(Set<EquipementMaison> equipements) {
        if (equipements == null || equipements.isEmpty()) return null;
        return equipements.stream().sorted().map(Enum::name).collect(Collectors.joining(","));
    }

    @Override
    public Set<EquipementMaison> convertToEntityAttribute(String valeur) {
        Set<EquipementMaison> equipements = EnumSet.noneOf(EquipementMaison.class);
        if (valeur == null || valeur.isBlank()) return equipements;
        Arrays.stream(valeur.split(","))
                .map(String::trim)
                .filter(code -> Arrays.stream(EquipementMaison.values()).anyMatch(e -> e.name().equals(code)))
                .map(EquipementMaison::valueOf)
                .forEach(equipements::add);
        return equipements;
    }
}
