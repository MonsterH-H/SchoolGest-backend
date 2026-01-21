# 🚩 Module Présences & Assiduité

### 📝 Résumé des Capacités
Ce module est le **garant de la discipline et de l'assiduité**. Il permet de digitaliser le rituel de l'appel et d'offrir un suivi transparent aux élèves et aux parents. Au-delà du simple pointage, il gère un **écosystème de justificatifs numériques** (scans de certificats médicaux, etc.) et possède un **cerveau préventif** qui analyse le comportement de l'etudiant pour générer des alertes de danger avant que la situation scolaire ne devienne irrécupérable.

---

## 🚀 Points Clés de Gestion
- **Pointage Temps Réel** : Interface de saisie rapide pour les enseignants lors des cours.
- **Dématérialisation des Justificatifs** : Workflow complet de dépôt (etudiant) et de validation (Admin/Validation cloud).
- **Audit de l'Assiduité** : Calcul automatique des taux de présence pour les bilans de fin d'année.
- **Notifications de Risque** : Génération d'alertes automatiques basées sur les cumuls d'absences.

---

## 🗂️ Modèle de Données (Entités)
- **Attendance (Pointage)** : Status (`PRESENT`, `ABSENT`, `RETARD`) + Preuve cloud.
- **AbsenceAlert** : Notification système stockée récapitulant la gravité du cas.

---

## 📡 Spécifications des APIs

| Méthode | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/presences/marquer` | Saisie de présence (souvent via interface mobile/tablette pro). |
| `POST` | `/api/presences/{id}/justifier` | Dépôt de fichier par l'élève (Multipart cloud). |
| `PATCH`| `/api/presences/{id}/valider-justificatif` | Décision administrative sur une absence. |
| `GET`  | `/api/presences/stats/etudiant/{id}` | Tableau de bord de ponctualité de l'élève. |

---

## ⚖️ Système de Gravité (Alertes)
Le module gère 4 niveaux de priorité basés sur les absences **non justifiées** :
1. **INFO** (1 absence) : Information simple.
2. **WARNING** (3 absences) : Avertissement envoyé.
3. **DANGER** (5 absences) : etudiant sous surveillance.
4. **DISCIPLINARY** (10 absences) : Transfert automatique au conseil de discipline.
