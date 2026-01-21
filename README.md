# 📔 GUIDE MAÎTRE - SchoolGest ERP (Complet)

Ce document est la source unique de vérité pour l'ensemble du projet **SchoolGest**. Il regroupe la vision, l'architecture, la sécurité, et les instructions de déploiement.

---

## 🌟 1. Vision et Objectifs
**SchoolGest** est un système de gestion scolaire (ERP) de nouvelle génération conçu pour automatiser les tâches administratives, pédagogiques et de communication.
- **Zéro Papier** : Digitalisation complète des absences, notes et devoirs.
- **Logistique Intelligente** : Moteur de détection de conflits pour l'emploi du temps.
- **Réussite Académique** : Suivi analytique des performances et génération automatique de bulletins.
- **Cloud-Native** : Stockage dématérialisé sur le cloud (Cloudinary).

---

## 🛠️ 2. Stack Technologique Full-Option
- **Framework** : Spring Boot 3.4.1 (Java 17+)
- **Base de données** : PostgreSQL (Persistance relationnelle robuste)
- **Sécurité** : 
    - Spring Security
    - JWT (JSON Web Tokens) pour l'authentification Stateless
    - BCrypt pour le hachage des mots de passe
- **Stockage Objets** : API Cloudinary (Images, PDF, Vidéos)
- **Outils** : 
    - Lombok (Réduction du code boilerplate)
    - Jackson (Parsing JSON avancé)
    - Maven (Gestion des dépendances)

---

## 📁 3. Architecture du Code (Détail des Dossiers)
Le projet suit une organisation **Modulaire par Domaine** :

- `entity/` : Modèles de données globaux (User, Student, Subject, etc.).
- `repository/` : Interfaces d'accès à la base de données (Spring Data JPA).
- `security/` : Configuration JWT, Filtres de sécurité et Custom User Details.
- `exception/` : Gestionnaire d'erreurs centralisé (`GlobalExceptionHandler`).
- **`gestions_...`** (Modules Métier) :
    - `auth/` : Inscription, Login, Reset Password.
    - `academique/` : Classes, Matières, Inscriptions, Bulletins.
    - `emploidutemps/` : Planning, Salles, Créneaux.
    - `presences/` : Appel, Justificatifs, Alertes d'absences.
    - `travaux/` : Devoirs, Rendus, Corrections.
    - `ressources/` : Supports de cours et **FileUpload Service Cloud**.
    - `communications/` : Messagerie et Notifications.
    - `admin/` : Dashboard et Statistiques.

---

## 🛡️ 4. Système de Sécurité et Rôles (RBAC)
Le projet gère 3 niveaux d'accès distincts :

1. **ADMIN** (Le Maître du Système) :
   - Accès au Dashboard global.
   - Création de la structure (Salles, Classes, Matières).
   - Validation finale des bulletins et inscriptions.
2. **ENSEIGNANT** (Le Pilote Pédagogique) :
   - Saisie des notes et présence.
   - Création de devoirs et partage de ressources.
   - Correction des rendus.
3. **ETUDIANT** (Le Bénéficiaire) :
   - Consultation de son planning et ses notes.
   - Rendu de devoirs et dépôt de justificatifs.
   - Messagerie privée avec les enseignants.

---

## ☁️ 5. Intégration Cloud (Cloudinary)
Le système n'enregistre aucun fichier localement. Tout passe par Cloudinary :
- **Dossiers automatiques** : `/avatars`, `/devoirs`, `/cours`, `/justificatifs`.
- **Performance** : Les fichiers sont servis via CDN pour une rapidité maximale.

---

## 🚀 6. Installation et Déploiement (Pas à Pas)

### Étape 1 : Base de données
Créez une base PostgreSQL nommée `gestschool_db`.

### Étape 2 : Configuration (`application.properties`)
Remplissez les clés suivantes :
```properties
# Cloudinary (Obligatoire)
cloudinary.cloud_name=dqroutsjq
cloudinary.api_key=733418768836493
cloudinary.api_secret=RRnVOtL9QwmT7Xdrsl52jhVsAz0

# JWT (Générez une clé forte de 64 caractères)
jwt.secret=votre_cle_secrete
```

### Étape 3 : Lancement
```bash
mvn clean install
mvn spring-boot:run
```

---

## 📊 7. Catalogue des APIs Majeures (Résumé)
- **Authentification** : `POST /api/auth/login`
- **Profil** : `PUT /api/auth/profile` (Multipart pour photo)
- **Planning** : `GET /api/emploidutemps/classe/{id}`
- **Note** : `POST /api/evaluations/notes`
- **Bulletin** : `POST /api/bulletins/generer`
- **Devoir** : `POST /api/travaux/devoirs/{id}/rendre`
- **Stats Admin** : `GET /api/admin/dashboard/stats`

---

## 📖 8. Glossaire Métier
- **UE (Unité d'Enseignement)** : Un module regroupant plusieurs matières.
- **EC (Élément Constitutif)** : Une matière individuelle.
- **ECTS (Crédits)** : Valeur numérique de la charge de travail d'un module.
- **Attendance Alert** : Système automatique notifiant l'etudiant à 1, 3, 5 et 10 absences.
- **Audit Trail** : Historique complet des actions (Logs) pour la sécurité.

---

## 🛠️ 9. Maintenance et Évolution
Le projet est conçu pour être **extensible**. Pour ajouter un nouveau module :
1. Créez les entités dans `entity/`.
2. Créez un package `gestions_nouveau/` avec Controller et Service.
3. Injectez le `FileUploadService` si vous avez besoin de stocker des fichiers.
4. Mettez à jour `SecurityConfig` pour les droits d'accès.

---
**Document rédigé le 23 Décembre 2025 - Fin de la phase de développement Backend.**
