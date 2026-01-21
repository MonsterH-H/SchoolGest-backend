# 📝 Module Devoirs & Ressources

### 📝 Résumé des Capacités
Ce module constitue l'**Espace Numérique de Travail (ENT)** du projet. Il gère la dématérialisation des contenus pédagogiques. Il permet aux enseignants de diffuser leurs savoirs (Supports de cours, vidéos) et de manager les productions des élèves. C'est ici qu'intervient la gestion des **délais (deadlines)** et des **rendus physiques**. Le module est nativement connecté au Cloud pour assurer qu'aucun fichier ne soit perdu et que les devoirs soient accessibles 24h/24.

---

## 🚀 Points Clés de Gestion
- **Diffusion de Savoirs** : Espace sécurisé de partage de fichiers (PDF, Liens, Vidéos) organisé par classe.
- **Cycle de Vie des Devoirs** : De la publication de l'énoncé à la remise du travail par l'élève.
- **Surveillance des Délais** : Détection automatique des rendus en retard par rapport à la date de clôture.
- **Feedback & Correction** : Espace dédié aux retours pédagogiques et à la notation des travaux remis.

---

## 🗂️ Modèle de Données (Entités)
- **Assignment** : Le sujet du devoir avec sa date limite et son barème.
- **Submission** : Le travail rendu par l'élève (Lien cloud + Texte).
- **Resource** : Le support de cours (Cours magistral, TP guide).

---

## 📡 Spécifications des APIs

- `POST /api/travaux/devoirs` : Publication d'un sujet (Multipart-Cloud).
- `POST /api/travaux/devoirs/{id}/rendre` : Rendu de l'etudiant avec horodatage.
- `GET /api/travaux/devoirs/{id}/soumissions` : Interface de correction pour le prof.
- `PATCH /api/travaux/soumissions/{id}/noter` : Attribution du feedback.

---

## ☁️ Gestion des Fichiers
- **Automatisation** : Le système sépare les fichiers dans des dossiers Cloudinary distincts (`/devoirs`, `/soumissions`, `/cours`) pour une organisation propre.
- **Disponibilité** : Génération d'URLs sécurisées HTTPS pour tous les téléchargements.
