# CAHIER DES CHARGES FINAL : PLATEFORME DE PROMOTION IMMOBILIÈRE ET D'AMÉNAGEMENT FONCIER AGRÉÉ DU MALI

**Modèle de Référence : Sociétés d'Équipement et Promoteurs Immobiliers Agréés par l'État (Type SEMA SA / SIFMA / ACI)**

---

## SOMMAIRE

1. [Contexte et Modèle Métier de Référence (SEMA)](#1-contexte-et-modèle-métier-de-référence-sema)
2. [Vision du Produit et Principes Directeurs](#2-vision-du-produit-et-principes-directeurs)
3. [Acteurs, Rôles et Gouvernance B2B](#3-acteurs-rôles-et-gouvernance-b2b)
4. [Homologation KYC & Procédure de Validation par l'Admin](#4-homologation-kyc--procédure-de-validation-par-ladmin)
5. [Régime de Publication et Gestion Souveraine des Litiges](#5-régime-de-publication-et-gestion-souveraine-des-litiges)
6. [Le Portefeuille des Promoteurs Agréés & Commodités de Proximité](#6-le-portefeuille-des-promoteurs-agréés--commodités-de-proximité)
7. [Le Plan de Masse Interactif (Master Plan)](#7-le-plan-de-masse-interactif-master-plan)
8. [Parcours de l'Application Mobile & Expérience Utilisateur](#8-parcours-de-lapplication-mobile--expérience-utilisateur)
9. [Prestation "Construire sur mon Terrain" (Offre Clé SEMA)](#9-prestation-construire-sur-mon-terrain-offre-clé-sema)
10. [Processus de Réservation, Visites et Assignation aux Agents](#10-processus-de-réservation-visites-et-assignation-aux-agents)
11. [Backlog Détaillé des Exigences Fonctionnelles (MoSCoW)](#11-backlog-détaillé-des-exigences-fonctionnelles-moscow)
12. [Recueil des User Stories Spécifiques](#12-recueil-des-user-stories-spécifiques)
13. [Exigences Non-Fonctionnelles & Sécurité](#13-exigences-non-fonctionnelles--sécurité)
14. [Architecture Technique & Stack Technologique](#14-architecture-technique--stack-technologique)
15. [Planning et Roadmap du Projet](#15-planning-et-roadmap-du-projet)

---

## 1. CONTEXTE ET MODÈLE MÉTIER DE RÉFÉRENCE (SEMA)

### 1.1 Contexte Foncier au Mali

Face à l'insécurité foncière chronique à Bamako et dans ses cercles périphériques (Kati, Mandé, Sanankoroba, Dialakoroboda, Safo), marquée par les litiges de voisinage, les faux actes coutumiers et les doubles attributions, les acquéreurs nationaux et les membres de la diaspora recherchent la sécurité absolue offerte par les **sociétés de promotion immobilière et d'aménagement foncier agréées par l'État** (à l'instar de la **SEMA SA - Société d'Équipement du Mali**, de la SIFMA SA ou de l'ACI).

### 1.2 Le Modèle Métier de la SEMA retenu pour la Plateforme

Ces sociétés opèrent selon un standard d'excellence et de traçabilité juridique :

1. **Création de Cités Résidentielles Sécurisées & Lotissements Réguliers :** Acquisition de grands domaines fonciers sous Titre Foncier (TF) mère, arrêté ministériel d'approbation de lotissement, découpage en parcelles régulières et réalisation des voiries et réseaux divers (VRD : bitumage ou reprofilage des voies, adduction d'eau potable SOMAPEP, raccordement au réseau électrique EDM, drainage des eaux pluviales).
2. **Vente de Parcelles Viabilisées avec Titre Foncier (TF) :** Vente de parcelles prêtes à bâtir, nées de lotissements officiels.
3. **Vente de Logements Clés en Main :** Villas économiques, moyen et haut standing (F3, F4, F5, duplex) au sein de cités viabilisées, clôturées et gardées.
4. **Vente sur Plan (VEFA) :** Vente en l'état futur d'achèvement.
5. **Construction sur le Terrain Fourni par le Client :** Formule phare où le particulier fournit sa propre parcelle déjà titrée (TF individuel), et la SEMA assure la conception des plans d'architecte, l'obtention du permis de construire et l'exécution du chantier, avec un schéma de paiement échelonné négocié (ex: 75% au rythme d'avancement des travaux, et solde de 25% financé sur 24 à 36 mois).

---

## 2. VISION DU PRODUIT ET PRINCIPES DIRECTEURS

### 2.1 Description Générale

La plateforme **Foncier+** est un carrefour numérique de confiance composé de quatre interfaces complémentaires :

- **Une Application Mobile (Flutter) & Web Public :** Permettant aux acquéreurs (résidents maliens et diaspora) de découvrir les cités, explorer le plan de masse interactif, vérifier les équipements VRD et commodités à proximité, réserver des lots, planifier des visites et soumettre un projet de construction sur leur propre terrain.
- **Un Dashboard Société / Direction (Web & Desktop Angular) :** Géré par l'**Agent Promoteur Responsable** (Directeur / Gérant). Il permet de piloter l'ensemble de l'entreprise, de gérer les programmes et lots, de créer les comptes des agents collaborateurs et d'assigner les demandes de visites et réservations.
- **Un Dashboard Agent Collaborateur (Mobile Flutter & Desktop Web) :** Permettant aux agents de terrain et commerciaux de consulter les visites et réservations qui leur ont été assignées par le responsable, de mener les visites guidées, de contacter les prospects et d'actualiser les statuts des dossiers.
- **Une Console d'Administration Plateforme (Web Angular) :** Pour l'administrateur Foncier+, dédiée au contrôle des dossiers KYC PDF ministériels et à la surveillance proactive de la transparence foncière.

### 2.2 Principes Directeurs

- **Confiance Institutionnelle & Transparence :** Les sociétés agréées par l'État procédant à des vérifications juridiques rigoureuses de leurs titres, la commercialisation s'appuie sur des garanties légales incontestables.
- **Égalité Stricte entre Acquéreurs (Mali & Diaspora) :** Aucune différence de droits ou de fonctionnalités entre un acquéreur résidant sur place et un membre de la diaspora. Les deux bénéficient exactement des mêmes spécificités : consultation des prix transparents, visite 360°, plan de masse interactif, réservation et suivi de projet.
- **Hiérarchie et Délégation Opérationnelle :** L'entreprise est engagée par son Responsable légal, mais l'exécution des visites et l'accueil client peuvent être délégués de manière fluide à des agents opérationnels.
- **Capacité de Suspension Immédiate en Cas de Litige :** Pour maintenir une confiance sans faille, **la société de promotion ET l'administrateur de la plateforme ont le pouvoir de geler / rendre indisponible immédiatement** tout lot ou programme dès l'apparition du moindre litige.
- **Pas de Paiement en Ligne (V1) :** Les aspects financiers et contractuels finaux sont conclus physiquement au siège du promoteur ou devant notaire.

---

## 3. ACTEURS, RÔLES ET GOUVERNANCE B2B

### 3.1 Matrice des Acteurs

| Acteur | Nature & Statut | Rôle & Missions Principales | Interfaces Utilisées |
| :--- | :--- | :--- | :--- |
| **Prospect / Acquéreur** | Personne Physique | Particulier (résident au Mali ou membre de la diaspora). **Spécificités et droits 100% identiques** pour tous les acquéreurs : recherche géolocalisée, consultation des commodités, plan de masse interactif, visite 360°, demande de réservation, demande de visite / RDV bimodal, suivi de dossier "Construire sur mon terrain". | Application Mobile (Flutter) & Web Public |
| **Société Promotrice Agréée** (`SocietePromotrice`) | **Personne Morale** | Entité juridique titulaire de l'agrément ministériel, du NIF et du RCCM (ex: SEMA SA). Propriétaire institutionnelle des programmes et parcelles. Ne s'authentifie pas directement en tant qu'humain, mais est représentée par son Responsable et regroupe ses agents collaborateurs. | Entité légale rattachée aux programmes |
| **Agent Promoteur Responsable** (`AgentPromoteur`, `estResponsableSociete = true`) | Personne Physique | **Directeur Général, Gérant ou Dirigeant légal** de la société promotrice. Personne physique obligatoire lors de la création de la société. Il gère le **Dashboard Société**, soumet le **dossier KYC PDF**, publie les programmes, **crée les comptes des agents collaborateurs** et **assigne les demandes de visites et réservations aux agents**. | Dashboard Société (Web & Desktop Angular) |
| **Agent Promoteur Collaborateur** (`AgentPromoteur`, `estResponsableSociete = false`) | Personne Physique | **Commercial ou Agent de terrain**, créé par le Responsable. Il dispose de son propre compte et accède à son **Dashboard Agent**. Il reçoit les visites et réservations qui lui sont assignées, conduit les visites sur chantier ou au siège, contacte les prospects et met à jour l'avancement. | Dashboard Agent (Mobile Flutter & Desktop Web) |
| **Administrateur Foncier+** (`Administrateur`) | Personne Physique | Garant de la conformité réglementaire de la plateforme. Contrôle et valide le **dossier KYC PDF** téléversé par les promoteurs, audite les agréments ministériels et détient le pouvoir souverain de suspension de lot ou programme en cas de litige. | Console d'Administration Web (Angular) |

### 3.2 Règles d'Éligibilité B2B & Règles de Gestion des Comptes

- **R01 (Agrément obligatoire) :** Seules les entreprises détenant un **Agrément officiel de Promoteur Immobilier délivré par le Ministère de l'Urbanisme et de l'Habitat du Mali** (avec NIF et RCCM vérifiés) sont autorisées à créer un compte société promotrice.
- **R02 (Obligation de l'Agent Responsable) :** La création d'une société promotrice est indissociable de la création de son **Agent Promoteur Responsable** (`estResponsableSociete = true`). C'est cette personne physique qui porte la responsabilité légale du compte.
- **R03 (Création et Délégation aux Agents) :** Seul l'Agent Responsable a le pouvoir de créer de nouveaux comptes `AgentPromoteur` rattachés à sa société. Le Responsable peut leur déléguer des tâches opérationnelles précises (gestion des visites, instruction des dossiers).
- **R04 (Exclusion de l'informel) :** Les démarcheurs informels (coxeurs) et les particuliers vendeurs de parcelles non viabilisées sont **strictement exclus** de la plateforme.
- **R05 (Égalité des Acquéreurs) :** Le modèle de données ne segmente pas techniquement les prospects : un compte `Acquereur` donne accès à la totalité du catalogue, des visites 360°, des réservations et des services pour les locaux comme pour la diaspora.

---

## 4. HOMOLOGATION KYC & PROCÉDURE DE VALIDATION PAR L'ADMIN

Afin d'éradiquer toute tentative de fraude foncière et de rassurer pleinement les acquéreurs et la diaspora, la plateforme applique une procédure stricte de conformité KYC (*Know Your Customer*) :

### 4.1 Téléversement Obligatoire du Dossier KYC PDF

Lors de l'inscription de la société promotrice par son Agent Responsable :
1. **Saisie des identifiants légaux (champs texte) :** Renseignement obligatoire de la raison sociale, du NIF, du RCCM, de l'adresse du siège, du téléphone standard et du Numéro d'Agrément ministériel.
2. **Téléversement d'un Document PDF Unique consolidé (`documentKycUrl`) :** L'agent responsable doit téléverser un dossier scanné complet sous format PDF comprenant obligatoirement :
   - La copie certifiée de l'**Arrêté d'Agrément de promoteur immobilier** délivré par le Ministère de l'Urbanisme et de l'Habitat.
   - La copie de l'**Attestation d'immatriculation fiscale (NIF)** délivrée par la Direction Générale des Impôts (DGI).
   - L'**Extrait du Registre du Commerce et du Crédit Mobilier (RCCM)** récent délivré par le Tribunal de Commerce.
   - La copie de la **Pièce d'identité officielle du Responsable légal**.
3. **Mise sous statut d'attente :** Dès soumission, la société et le compte de son responsable reçoivent le statut `statutAgrement = EN_ATTENTE_VALIDATION`. **Aucune publication de programme ni de lot n'est possible** à ce stade.

### 4.2 Workflow d'Instruction et de Validation par l'Administrateur

1. **Notification et Prise en charge :** L'Administrateur Foncier+ reçoit une notification de nouvelle demande d'adhésion sur son tableau de bord d'administration.
2. **Vérification croisée :** L'Administrateur ouvre le dossier, consulte le document PDF téléversé, et vérifie la conformité des numéros NIF, RCCM et de l'arrêté ministériel.
3. **Décision Administrative :**
   - **Validation (`VALIDE_MINISTERE`) :** Si le dossier est authentique, l'administrateur valide le compte. La société reçoit le label **« Promoteur Agréé & Vérifié »** et l'agent responsable peut immédiatement créer ses programmes et ajouter ses collaborateurs.
   - **Rejet (`REJETE`) :** Si un document est falsifié, expiré ou incomplet, l'administrateur rejette le dossier avec motif d'explication. La société est notifiée et ne peut en aucun cas publier sur la plateforme.

---

## 5. RÉGIME DE PUBLICATION ET GESTION SOUVERAINE DES LITIGES

### 5.1 Publication Directe par les Promoteurs Agréés

Les promoteurs agréés (comme la SEMA SA) bénéficiant de garanties juridiques et techniques de premier ordre (Titre Foncier mère vérifié, décret ou arrêté ministériel d'approbation de lotissement, bornage géométrique DNDC) :

- **Publication Immédiate :** Tout programme ou lotissement créé par une société homologuée est directement publié et rendu visible aux acquéreurs dès lors que l'agrément ministériel est validé.
- **Suppression du délai d'attente :** Aucun délai bloquant préalable de 72h n'est imposé pour la publication.

### 5.2 Mécanisme de Suspension et de Mise en Indisponibilité pour Litige

Si une contestation de limite, un chevauchement parcellaire, une contestation successorale ou une procédure judiciaire apparaît sur une parcelle :

1. **Action du Promoteur (Auto-régulation) :** Le responsable de la société promotrice peut, en un clic depuis son tableau de bord, basculer le lot ou la cité au statut **`INDISPONIBLE_LITIGE`** avec motif interne.
2. **Action de l'Administrateur (Contrôle Souverain) :** L'administrateur de Foncier+, saisi par un signalement, une décision judiciaire ou un constat administratif, a le pouvoir de suspendre un lot ou un programme entier de manière souveraine.
3. **Conséquence Immédiate :** Le lot devient grisé/hachuré sur le plan de masse interactif, toute tentative de réservation ou de demande de visite est bloquée, et une mention d'indisponibilité temporaire protège les acquéreurs.
4. **Levée de Suspension :** Seul l'acteur ayant initié le blocage (ou l'administrateur après résolution du litige) peut réactiver la disponibilité du bien (`DISPONIBLE`).

---

## 6. LE PORTEFEUILLE DES PROMOTEURS AGRÉÉS & COMMODITÉS DE PROXIMITÉ

Une société de promotion et d'aménagement agréée (comme la SEMA SA, la SIFMA ou l'ACI) gère un modèle économique dual reposant sur deux piliers d'offre :

### 6.1 Pilier 1 : Portefeuille Multi-Cités & Grands Lotissements Groupés (TF Mère)

- **Détention de Plusieurs Cités Simultanées :** Une même société peut détenir, développer et commercialiser simultanément plusieurs programmes ou cités résidentielles (ex: pour la SEMA : _Cité Espoir Samanko_, _Cité Sotuba_, _Cité Dialakoroboda_).
- **Infrastructures Groupées (VRD) :** Chaque cité repose sur un Titre Foncier (TF) Mère approuvé par arrêté ministériel, avec son propre plan de masse interactif découpé en lots viabilisés (adduction générale d'eau potable SOMAPEP, transformateurs et réseau électrique EDM, voies bitumées ou pavées, caniveaux d'évacuation des eaux de pluie).
- **Typologie des Biens en Cité :** Lots viabilisés nus prêts à bâtir et logements clés en main (Villas F3, F4, F5, Duplex).

### 6.2 Pilier 2 : Stock Diffus de Parcelles Individuelles Viabilisées (TF Individuels Directs)

- **Détention d'un Volume Important de Parcelles Individuelles :** En dehors des cités fermées, la société acquiert et gère un parc diffus de parcelles dispersées dans différentes communes ou cercles (ex: 15 parcelles à Yirimadio, 10 à Souleymanebougou, 25 à Sanankoroba, 8 à Kati).
- **Titre Foncier (TF) Individuel Garanti :** Chaque parcelle dispose de son propre numéro de TF individuel inattaquable, enregistré au livre foncier local.
- **Opérations Actives de Viabilisation par la Société :** La société ne commercialise pas de terrains bruts : elle réalise les travaux de viabilisation essentiels avant ou pendant la vente :
  - Implantation des 4 bornes géodésiques en béton par géomètre assermenté.
  - Raccordement ou extension du réseau d'eau potable SOMAPEP (compteur ou borne fontaine en façade).
  - Raccordement ou poteau électrique EDM en bordure de parcelle.
  - Réalisation d'une clôture de sécurisation ou d'un soubassement en dur.
- **Commercialisation Directe :** Ces parcelles individuelles sont publiées sur la plateforme avec leur fiche technique complète, photos réelles, localisation GPS exacte, et peuvent faire l'objet de demandes de réservation et de visites sur place.

### 6.3 Vente sur Plan (VEFA) et Construction Clé en Main

En complément, les promoteurs proposent la vente sur plan et la réalisation de logements sur commande au sein de leurs programmes.

### 6.4 Gestion des Commodités et Équipements de Proximité (`Commodite`)

Pour valoriser le cadre de vie et permettre à l'acquéreur de projeter son quotidien en toute transparence, chaque bien foncier (lot en cité ou parcelle individuelle) et programme est relié à des **commodités de proximité géolocalisées** avec leur **distance réelle en kilomètres (`distance_km`)** :

- 🏫 **Établissements Scolaires & Éducation :** Écoles primaires, collèges, lycées et centres de formation (ex: *Lycée public à 1.1 km*, *École primaire privée à 450 m*).
- 🏥 **Santé & Soins Médicaux :** CSCOM (Centres de Santé Communautaires), hôpitaux de district, cliniques privées, pharmacies de garde (ex: *CSCOM à 800 m*).
- 🛒 **Commerces & Marchés :** Marchés traditionnels de vivres, supermarchés, supérettes de proximité (ex: *Marché central de quartier à 600 m*).
- 🕌 **Lieux de Culte :** Mosquées de quartier, grandes mosquées de vendredi, églises (ex: *Mosquée à 300 m*).
- 🛡️ **Sécurité & Services Administratifs :** Commissariats de police, brigades de gendarmerie, mairies d'arrondissement (ex: *Poste de police à 1.8 km*).
- 🛣️ **Accessibilité & Transports :** Axes goudronnés principaux, arrêts de minibus Sotrama, gares routières (ex: *Voie bitumée principale à 150 m*).

---

## 7. LE PLAN DE MASSE INTERACTIF (MASTER PLAN)

Composant d'excellence de l'expérience utilisateur Foncier+ :

1. **Représentation Graphique Intuitive :** Affichage d'un plan de lotissement d'urbanisme découpé en îlots et lots numérotés en format vectoriel SVG fluide.
2. **Statut Couleur Dynamique des Lots :**
   - 🟢 **Vert : Disponible** (libre à l'achat / réservation).
   - 🟡 **Orange : Réservé** (option posée en cours d'instruction par un agent promoteur).
   - 🔴 **Rouge : Vendu** (parcelle définitivement actée / chantier en cours).
   - ⚫ / ⚠️ **Gris Hachuré : Indisponible / Litige** (parcelle gelée par la société ou par l'administrateur).
3. **Fiche Pop-up au Clic sur un Lot :**
   - Numéro du lot et superficie exacte ($m^2$).
   - Dimensions (façade $	imes$ profondeur).
   - Détail des équipements VRD (adduction SOMAPEP, EDM, voirie).
   - Prix officiel homologué en FCFA.
   - Bouton d'action directe : **« Formuler une demande de réservation »** (désactivé si le lot est réservé, vendu ou indisponible).

---

## 8. PARCOURS DE L'APPLICATION MOBILE & EXPÉRIENCE UTILISATEUR

L'application mobile (développée en Flutter) propose un parcours utilisateur complet, fluide et rassurant, validé sur le prototype interactif :

1. **Écran Splash & Identité de Marque :** Présentation de la marque Foncier+, logo officiel et signature : *« L'Immobilier et le Foncier Agréé au Mali - Zéro Litige »*.
2. **Onboarding Pédagogique en 3 Étapes :**
   - *Étape 1 : Zéro Litige Garanti* – Partenariats exclusifs avec les promoteurs agréés par l'État (SEMA, SIFMA, etc.).
   - *Étape 2 : Titres Fonciers Vérifiés* – Parcelles titrées (TF), bornées et viabilisées (Eau SOMAPEP, Électricité EDM).
   - *Étape 3 : Accompagnement Complet* – De la réservation sur le plan de masse jusqu'à la remise des clés chez le notaire.
3. **Page d'Accueil & Découverte Multi-critères :**
   - Bannière des promoteurs certifiés avec badge officiel.
   - Onglets de bascule rapide : **« Cités & Lotissements »** vs **« Parcelles Individuelles »**.
   - Accès direct en un clic au module phare : **« Construire sur mon terrain »**.
   - Filtres de recherche intuitifs : Localisation (Kati, Samanko, Sanankoroba...), budget max (FCFA), superficie ($m^2$).
4. **Fiche Détail du Bien & Commodités de Proximité :**
   - Carrousel de photos réelles haute définition du site et des réalisations.
   - Caractéristiques techniques : TF Mère ou TF Individuel, superficie, viabilisation VRD.
   - **Bloc Commodités :** Liste claire avec pictogrammes et distances en kilomètres (écoles, santé, marchés, transports).
   - Bouton d'accès au **Plan de masse interactif** ou à la **Visite 360°**.
5. **Visite Immersive 360° :** Visionneuse panoramique interactive permettant aux acquéreurs locaux comme à la diaspora de visiter virtuellement les voies de la cité et les villas témoins comme s'ils y étaient.
6. **Formulaire de Demande de Réservation Horodatée :**
   - Récapitulatif clair du bien sélectionné, du prix officiel et des modalités.
   - Formulaire d'identité simplifié (sans paiement en ligne bancaire ou mobile money en V1).
   - Notification instantanée de prise en compte avec génération d'un numéro de dossier.
7. **Module de Prise de Rendez-vous Bimodal (Visite) :**
   - Choix du type de rendez-vous : **Au Siège** (direction commerciale du promoteur) ou **Visite sur Chantier**.
   - Sélection du jour et du créneau horaire souhaité.
8. **Espace Personnel de l'Acquéreur :**
   - Onglet **« Mes Réservations »** : Suivi de l'état du dossier en temps réel (*En attente*, *Confirmée*, *Rendez-vous fixé*, *Finalisée au siège*).
   - Onglet **« Mes Rendez-vous »** : Rappel des créneaux validés avec nom de l'agent assigné et coordonnées GPS.
   - Onglet **« Mes Favoris »** : Sauvegarde des biens et programmes pour comparaison.

---

## 9. PRESTATION "CONSTRUIRE SUR MON TERRAIN" (OFFRE CLÉ SEMA)

Offre destinée aux particuliers et expatriés propriétaires d'une parcelle avec Titre Foncier individuel :

1. **Formulaire Client Dédié :**
   - Renseignement du N° de Titre Foncier (TF) individuel et localisation de la parcelle.
   - Superficie du terrain ($m^2$).
   - Sélection du modèle de villa souhaité (Villa F3 économique, F4 standing, Villa duplex F5...).
   - Sélection de la société promotrice partenaire choisie (ex: SEMA SA).
2. **Instruction par la Société Promotrice :**
   - Réception du dossier technique dans le Dashboard Société.
   - Le responsable assigne le dossier à un agent commercial/technique.
   - Prise de contact sous 48h et programmation d'une visite technique du terrain.
   - Élaboration d'une proposition architecturale et d'un devis financier avec facilités d'échelonnement (ex: paiement au fur et à mesure des étapes de gros œuvre et second œuvre).

---

## 10. PROCESSUS DE RÉSERVATION, VISITES ET ASSIGNATION AUX AGENTS

Ce processus illustre la délégation concrète entre l'Agent Responsable et ses Agents Collaborateurs :

```
[ Prospect Acquéreur ]
       │
       ▼  (Émet une demande de visite sur chantier ou une réservation)
[ Flux Central de la Société Promotrice ]
       │
       ▼  (Arrive sur le Dashboard Société / Desktop)
[ Agent Promoteur Responsable (Directeur) ]
       │
       ├─ Option A : Gérer lui-même
       │
       └─ Option B : Déléguer à un collaborateur
              │
              ▼  (Le Directeur clique sur : [ Assigner à l'Agent 1 (ou Agent 2) ])
       [ Notification instantanée envoyée à l'Agent ]
              │
              ▼  (Visible sur son Dashboard Agent Mobile & Desktop)
       [ Agent Collaborateur (Agent 1) ]
              ├── Reçoit les coordonnées du client et la fiche du lot
              ├── Contacte le prospect par téléphone / message
              ├── Conduit la visite sur le terrain de la cité
              └── Met à jour le statut du dossier (ex: "Visite effectuée", "Réservé 🟡")
```

### 10.1 Cas d'Usage Concret : Gestion d'une Demande de Visite
1. **Émission par le Prospect :** Un client sélectionne la *Cité Espoir Samanko* et demande une visite sur le chantier pour le samedi à 10h00.
2. **Réception au Siège :** La demande s'affiche instantanément sur le **Dashboard Société** du Responsable.
3. **Dispatching par le Responsable :** Le Responsable, ayant de multiples obligations de direction, sélectionne la demande et l'assigne à l'**Agent 1 (Moussa Diarra)**.
4. **Prise en charge par l'Agent 1 :**
   - L'Agent 1 voit la notification sur son **Dashboard Mobile (ou Desktop)**.
   - L'Agent 1 confirme le créneau avec le client.
   - Le samedi, l'Agent 1 accueille le prospect sur le chantier, lui présente les bornes, les VRD et la villa témoin.
   - L'Agent 1 note le compte-rendu dans son application.

### 10.2 Traitement des Réservations de Lots
- Si le client confirme son intérêt d'achat :
  - L'Agent assigné bascule le lot au statut **Réservé 🟡** sur son interface, ce qui colore automatiquement le lot en jaune sur le plan de masse interactif public.
  - Le prospect est convié au siège pour le montage du dossier contractuel final.

---

## 11. BACKLOG DÉTAILLÉ DES EXIGENCES FONCTIONNELLES (MoSCoW)

### 11.1 Must Have (Indispensables - MVP V1)

- **Gestion des Rôles & Gouvernance Hiérarchique :** Acquéreurs (Mali & Diaspora avec spécificités 100% identiques), Sociétés Promotrices (Personne Morale), Agent Promoteur Responsable (Direction), Agents Promoteurs Collaborateurs (Commerciaux/Terrain), Administrateurs.
- **Création d'Agents & Dashboard Dédié :** Possibilité pour le Responsable de créer des comptes pour ses agents collaborateurs, et mise à disposition d'un **Dashboard Agent (Mobile et Desktop)** pour le traitement de leurs tâches.
- **Système d'Assignation / Dispatching :** Capacité pour le Responsable d'attribuer une demande de visite ou de réservation reçue par la société à un agent spécifique (Agent 1, Agent 2, etc.).
- **Homologation KYC Promoteurs via PDF :** Téléversement obligatoire du dossier PDF unique (Agrément ministériel, NIF, RCCM, CNI du responsable) et validation par l'Administrateur avant toute publication.
- **Gestion des Commodités de Proximité :** Association des équipements (écoles, santé, mosquées, commerces, transports) avec distance en kilomètres (`distance_km`) sur chaque bien foncier.
- **Parcours Mobile Complet :** Splash screen, Onboarding 3 étapes, Accueil multi-filtres, Plan de masse SVG dynamique, Fiche détail avec commodités, Visite 360°, Demande de réservation, Prise de RDV bimodal, Espace suivi.
- **Publication Directe des Programmes :** Saisie et mise en ligne immédiate des cités et parcelles par le promoteur agréé validé.
- **Plan de Masse Interactif :** Visualisation SVG des lots avec statuts couleurs dynamiques (🟢, 🟡, 🔴, ⚫).
- **Gestion Souveraine des Litiges :** Bouton de suspension immédiate d'un lot ou d'une cité pour litige (accessible au Responsable de Société et à l'Admin).
- **Demande de Réservation de Lot :** Horodatage, enregistrement et transmission sans paiement en ligne.
- **Prise de Rendez-vous Bimodale :** Choix Siège vs Chantier avec calendrier de créneaux.
- **Module "Construire sur mon terrain" :** Dépôt et suivi des projets de particuliers titulaires de TF.
- **Visite Immersive 360° :** Visionneuse panoramique Pannellum.js des cités et logements témoins.

### 11.2 Should Have (Améliorations Immédiates)

- Journal d'audit et historique des motifs de suspension de lot.
- Export PDF de la fiche récapitulative de réservation pour l'acquéreur.
- Suivi simplifié de l'avancement des VRD en pourcentage (ex: _Eau SOMAPEP : 90%_).
- Système de notifications push / SMS lors de l'assignation d'une visite à un agent.

### 11.3 Could Have (Perspectives Futures)

- Intégration de démarcheurs officiels accrédités par les sociétés promotrices.
- Journal de chantier en ligne avec photos de drone et jalons d'avancement.
- Modèle d'abonnement payant ou commission de mise en avant sur la plateforme.

### 11.4 Won’t Have (Exclus de la Phase 1)

- Paiement en ligne ou Mobile Money du montant d'acquisition des biens.
- Signature d'actes de vente en ligne (réservée aux études notariales et sièges des promoteurs).

---

## 12. RECUEIL DES USER STORIES SPÉCIFIQUES

### US-P01 : Inscription et Téléversement KYC PDF par le Responsable Promoteur
**En tant que** Directeur / Responsable d'une société promotrice (`AgentPromoteur`, `estResponsableSociete = true`),  
**Je veux** renseigner mes identifiants d'entreprise (NIF, RCCM, N° Agrément) et téléverser mon dossier PDF officiel KYC,  
**Afin que** l'administrateur puisse certifier notre statut de promoteur agréé par l'État.

### US-P02 : Instruction et Validation KYC par l'Administrateur
**En tant qu'** administrateur de la plateforme (`Administrateur`),  
**Je veux** télécharger et examiner le dossier PDF KYC soumis par une société promotrice pour vérifier son arrêté ministériel,  
**Afin de** valider son compte (`VALIDE_MINISTERE`) ou le rejeter avec motif pour protéger les acquéreurs.

### US-P03 : Création d'Agents Collaborateurs par le Responsable
**En tant que** Responsable de société promotrice,  
**Je veux** créer des comptes d'accès pour mes agents commerciaux et agents de terrain,  
**Afin de** leur donner un accès à leur propre dashboard (mobile/desktop) pour m'épauler dans la gestion quotidienne.

### US-P04 : Assignation d'une Demande de Visite par le Responsable
**En tant que** Responsable de société promotrice,  
**Je veux** recevoir les demandes de visite entrantes sur mon Dashboard Société et les attribuer à l'Agent 1 ou l'Agent 2,  
**Afin que** chaque prospect soit pris en charge rapidement par un agent disponible.

### US-P05 : Prise en Charge d'une Visite Assignée par l'Agent Collaborateur
**En tant qu'** agent promoteur collaborateur (`AgentPromoteur`, `estResponsableSociete = false`),  
**Je veux** consulter sur mon Dashboard (mobile ou desktop) les visites qui me sont attribuées par la direction,  
**Afin de** contacter le client, réaliser la visite sur le chantier et mettre à jour le statut du dossier.

### US-P06 : Traitement des Réservations et Changement de Statut
**En tant qu'** agent promoteur assigné,  
**Je veux** mettre à jour le statut d'un lot en "Réservé 🟡" après confirmation du prospect,  
**Afin de** bloquer la parcelle sur le plan de masse public et engager les formalités contractuelles.

### US-P07 : Consultation des Commodités et Distances par l'Acquéreur
**En tant qu'** acquéreur (résident local ou membre de la diaspora),  
**Je veux** voir la liste des commodités environnantes (écoles, hôpitaux, marchés, mosquées) avec leurs distances en kilomètres,  
**Afin d'** évaluer le cadre de vie et la praticité de la localisation avant de réserver.

### US-P08 : Consultation du Plan de Masse Interactif
**En tant qu'** acquéreur,  
**Je veux** explorer le plan de masse vectoriel d'une cité et cliquer sur chaque lot pour voir son statut couleur, son prix et sa viabilisation,  
**Afin de** choisir la parcelle qui correspond à mon budget et à mes critères.

### US-P09 : Demande de Réservation sans Paiement en Ligne
**En tant qu'** acquéreur,  
**Je veux** formuler une demande de réservation horodatée sur un lot disponible 🟢,  
**Afin d'** être contacté en priorité par le promoteur sans avoir à payer en ligne.

### US-P10 : Demande "Construire sur mon Terrain"
**En tant que** propriétaire d'une parcelle avec Titre Foncier individuel,  
**Je veux** déposer mon projet de construction en indiquant mon N° TF et le modèle de villa désiré,  
**Afin d'** obtenir un devis de construction avec facilités de paiement auprès d'une société reconnue.

### US-P11 : Suspension d'un Lot en Cas de Litige par le Promoteur ou l'Admin
**En tant que** promoteur agréé ou administrateur,  
**Je veux** pouvoir basculer immédiatement un lot contesté en statut `INDISPONIBLE_LITIGE`,  
**Afin de** geler toute réservation et préserver la sécurité juridique des acquéreurs.

---

## 13. EXIGENCES NON-FONCTIONNELLES & SÉCURITÉ

- **Disponibilité :** 99.8% de disponibilité opérationnelle.
- **Fluidité Visuelle :** Chargement instantané du plan de masse interactif SVG sur mobile (moins de 1,5 seconde).
- **Sécurisation des Documents KYC :** Stockage chiffré et accès restreint aux dossiers PDF d'agrément ministériel et aux pièces d'identité.
- **Traçabilité des Actions Critiques :** Enregistrement horodaté de toute mise en indisponibilité / suspension de lot (acteur, date, motif).
- **Protection des Données Personnelles :** Conformité avec la législation malienne sur la protection des données à caractère personnel (APDP).

---

## 14. ARCHITECTURE TECHNIQUE & STACK TECHNOLOGIQUE

- **Backend :** Spring Boot 3 (Java 17/21), Spring Data JPA, Spring Security (JWT).
- **Base de Données :** MySQL 8 (InnoDB, UTF8MB4).
- **Frontend Web Professionnel :** Angular 17+, Tailwind CSS (Dashboard Société, Dashboard Agent Desktop, Console Admin).
- **Frontend Mobile Public & Agent :** Flutter 3+ (Application Acquéreur & Dashboard Agent Mobile).
- **Visualisation Cartographique & 360° :** SVG interactif dynamique + Pannellum.js.

---

## 15. PLANNING ET ROADMAP DU PROJET

- **Phase 1 :** Cadrage, Cahier des Charges Finalisé & Diagramme de Classes UML (Validé).
- **Phase 2 :** Prototype Interactif Complet Mobile & Web (Validé).
- **Phase 3 :** Modèle Physique de Données (DDL MySQL) et Socle Backend Spring Boot (Validé).
- **Phase 4 :** Intégration du Plan de Masse Interactif et API REST.
- **Phase 5 :** Tests d'acceptation et Déploiement pilote.
