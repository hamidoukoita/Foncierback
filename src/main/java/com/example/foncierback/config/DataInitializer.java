package com.example.foncierback.config;

import com.example.foncierback.entity.Administrateur;
import com.example.foncierback.entity.enums.StatutCompte;
import com.example.foncierback.repository.AdministrateurRepository;
import com.example.foncierback.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final AdministrateurRepository administrateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.nom:Admin}")
    private String adminNom;

    @Value("${app.admin.prenom:FoncierPlus}")
    private String adminPrenom;

    @Value("${app.admin.telephone:70000000}")
    private String adminTelephone;

    @Value("${app.admin.mot-de-passe:admin1234}")
    private String adminMotDePasse;

    @Override
    @Transactional
    public void run(String... args) {
        initSuperAdmin();
    }

    private void initSuperAdmin() {
        if (!utilisateurRepository.existsByTelephone(adminTelephone)) {
            Administrateur admin = new Administrateur();
            admin.setNom(adminNom);
            admin.setPrenom(adminPrenom);
            admin.setTelephone(adminTelephone);
            admin.setMotDePasse(passwordEncoder.encode(adminMotDePasse));
            admin.setStatut(StatutCompte.ACTIF);
            admin.setDateCreation(LocalDateTime.now());

            administrateurRepository.save(admin);
            log.info(">>> Compte Administrateur initialisé avec succès ! Téléphone: {}", adminTelephone);
        } else {
            log.info(">>> Le compte administrateur ({}) existe déjà.", adminTelephone);
        }
    }
}
