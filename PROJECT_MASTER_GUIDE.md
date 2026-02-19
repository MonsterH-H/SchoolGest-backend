# 📔 GUIDE MAITRE - SchoolGest ERP (Complet)

Ce document est la source unique de verite pour l'ensemble du projet **SchoolGest**. Il regroupe la vision, l'architecture, la securite, et les instructions de deploiement.

---

## 🌟 1. Vision et Objectifs
**SchoolGest** est un systeme de gestion scolaire (ERP) de nouvelle generation concu pour automatiser les taches administratives, pedagogiques et de communication.
- **Zero Papier** : Digitalisation complete des absences, notes et devoirs.
- **Logistique Intelligente** : Moteur de detection de conflits pour l'emploi du temps.
- **Reussite Academique** : Suivi analytique des performances et generation automatique de bulletins.
- **Cloud-Native** : Stockage dematerialise sur le cloud (Cloudinary).

---

## 🛠️ 2. Stack Technologique
- **Framework** : Spring Boot 3.4.1 (Java 23)
- **Base de donnees** : PostgreSQL
- **Securite** : Spring Security + JWT + BCrypt
- **Stockage Objets** : Cloudinary
- **Outils** : Lombok, Jackson, Maven

---

## 📁 3. Architecture du Code
Organisation modulaire par domaine :
- `entity/`, `repository/`, `security/`, `exception/`
- **Modules metier** : `auth`, `academique`, `emploidutemps`, `presences`, `travaux`, `ressources`, `communications`, `admin`

---

## 🛡️ 4. Securite et Roles (RBAC)
- **ADMIN** : administration globale, validation finale
- **ENSEIGNANT** : notes, presences, devoirs
- **ETUDIANT** : consultation planning/notes, rendus

Details : `docs/SECURITY.md`

---

## ☁️ 5. Integration Cloud (Cloudinary)
Aucun fichier n'est stocke localement. Les dossiers cibles sont :
- `/avatars`, `/devoirs`, `/cours`, `/justificatifs`, `/messages`, `/soumissions`

---

## 🚀 6. Installation et Deploiement
Voir `SETUP_GUIDE.md` et `docs/DEPLOYMENT.md`.

Configuration (extrait) :
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/gestschool_db
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

jwt.secret=${JWT_SECRET}

cloudinary.cloud_name=${CLOUDINARY_CLOUD_NAME}
cloudinary.api_key=${CLOUDINARY_API_KEY}
cloudinary.api_secret=${CLOUDINARY_API_SECRET}
```

---

## 📊 7. Catalogue API (Resume)
- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `PUT /api/auth/profile`
- `GET /api/emploidutemps/classe/{id}`
- `POST /api/evaluations/notes`
- `POST /api/bulletins/generer`
- `POST /api/travaux/devoirs/{id}/rendre`
- `GET /api/admin/dashboard/stats`

Contrat complet : `docs/API_REFERENCE.md`

---

## 📖 8. Glossaire Metier
- **UE** : Unite d'Enseignement
- **EC** : Element Constitutif
- **ECTS** : Credits
- **Audit Trail** : Historique des actions (logs)

---

## 🛠️ 9. Maintenance et Evolution
Pour ajouter un module :
1. Creer les entites dans `entity/`.
2. Creer un package `gestions_nouveau/`.
3. Injecter `FileUploadService` si besoin.
4. Mettre a jour `SecurityConfig`.

---
**Document mis a jour le 19 fevrier 2026.**
