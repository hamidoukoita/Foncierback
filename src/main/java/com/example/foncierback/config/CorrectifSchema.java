package com.example.foncierback.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Le schéma est géré par ddl-auto=update, qui crée les colonnes d'enum en type ENUM MySQL
 * mais ne les modifie jamais ensuite : une nouvelle valeur d'enum Java y serait refusée.
 * Au démarrage, les colonnes concernées passent en VARCHAR (une seule fois, sans perte de données).
 */
@Component
@Order(0)
@RequiredArgsConstructor
@Slf4j
public class CorrectifSchema implements ApplicationRunner {

    private record Colonne(String table, String nom) {
    }

    /** Colonnes dont l'enum Java a gagné des valeurs depuis leur création. */
    private static final List<Colonne> COLONNES_ENUM = List.of(
            new Colonne("reservations", "statut")
    );

    private final JdbcTemplate jdbc;

    @Override
    public void run(ApplicationArguments args) {
        for (Colonne colonne : COLONNES_ENUM) {
            convertirEnVarchar(colonne.table(), colonne.nom());
        }
    }

    private void convertirEnVarchar(String table, String colonne) {
        try {
            List<String> types = jdbc.queryForList(
                    "SELECT DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS"
                            + " WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?",
                    String.class, table, colonne);
            if (!types.isEmpty() && "enum".equalsIgnoreCase(types.get(0))) {
                jdbc.execute("ALTER TABLE " + table + " MODIFY " + colonne + " VARCHAR(30) NOT NULL");
                log.info("Colonne {}.{} convertie de ENUM en VARCHAR(30).", table, colonne);
            }
        } catch (RuntimeException e) {
            log.warn("Correctif de schéma impossible pour {}.{} : {}", table, colonne, e.getMessage());
        }
    }
}
