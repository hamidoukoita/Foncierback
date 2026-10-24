package com.example.foncierback.config;

import com.example.foncierback.common.validation.Formats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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
            new Colonne("reservations", "statut"),
            new Colonne("projets_construction", "statut")
    );

    private final JdbcTemplate jdbc;

    @Override
    public void run(ApplicationArguments args) {
        for (Colonne colonne : COLONNES_ENUM) {
            convertirEnVarchar(colonne.table(), colonne.nom());
        }
        supprimerLiensExternes();
        retirerLibellesGeneriques();
        for (Colonne colonne : COLONNES_TITRE_FONCIER) {
            normaliserTitresFonciers(colonne.table(), colonne.nom());
        }
    }

    /** Colonnes qui portent un numéro de Titre Foncier. */
    private static final List<Colonne> COLONNES_TITRE_FONCIER = List.of(
            new Colonne("parcelles_individuelles", "numero_titre_foncier"),
            new Colonne("maisons_cle_en_main", "numero_titre_foncier"),
            new Colonne("projets_construction", "numero_titre_foncier"),
            new Colonne("programmes_fonciers", "numero_titre_mere"));

    /** Les numéros saisis avant la règle « TF- » (« TF 1024 », « tf r231 »…) sont ramenés au format commun. */
    private void normaliserTitresFonciers(String table, String colonne) {
        try {
            List<Map<String, Object>> lignes = jdbc.queryForList(
                    "SELECT id, " + colonne + " AS numero FROM " + table + " WHERE " + colonne + " IS NOT NULL");
            int corriges = 0;
            for (Map<String, Object> ligne : lignes) {
                String actuel = (String) ligne.get("numero");
                String normalise = Formats.titreFoncier(actuel);
                if (normalise != null && !normalise.equals(actuel)) {
                    corriges += jdbc.update("UPDATE " + table + " SET " + colonne + " = ? WHERE id = ?",
                            normalise, ligne.get("id"));
                }
            }
            if (corriges > 0) {
                log.info("{} numéro(s) de Titre Foncier mis au format « TF- » dans {}.", corriges, table);
            }
        } catch (RuntimeException e) {
            log.warn("Normalisation des Titres Fonciers impossible ({}) : {}", table, e.getMessage());
        }
    }

    /** L'ancien libellé par défaut « Pièce suivante » est remplacé par le nom de la pièce d'arrivée, calculé à l'affichage. */
    private void retirerLibellesGeneriques() {
        try {
            jdbc.update("UPDATE liens_panoramas SET libelle = NULL WHERE libelle = 'Pièce suivante'");
        } catch (RuntimeException e) {
            log.warn("Mise à jour des libellés de points impossible : {}", e.getMessage());
        }
    }

    /**
     * Les visites virtuelles par lien externe (Matterport, Kuula…) ont été retirées :
     * on efface les anciennes lignes, puis la colonne qui portait le lien.
     */
    private void supprimerLiensExternes() {
        try {
            int supprimes = jdbc.update("DELETE FROM medias_fonciers WHERE type = 'VISITE_VIRTUELLE'");
            if (supprimes > 0) {
                log.info("{} ancien(s) lien(s) externe(s) de visite virtuelle supprimé(s).", supprimes);
            }
            Integer colonne = jdbc.queryForObject(
                    "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS"
                            + " WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'medias_fonciers' AND COLUMN_NAME = 'lien_externe'",
                    Integer.class);
            if (colonne != null && colonne > 0) {
                jdbc.execute("ALTER TABLE medias_fonciers DROP COLUMN lien_externe");
                log.info("Colonne medias_fonciers.lien_externe supprimée.");
            }
        } catch (RuntimeException e) {
            log.warn("Suppression des liens externes impossible : {}", e.getMessage());
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
