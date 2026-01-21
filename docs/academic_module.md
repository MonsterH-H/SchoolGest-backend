# 🏫 Module Structure Académique

### 📝 Résumé des Capacités
Ce module est l'**architecte du campus virtuel**. Il définit le squelette sur lequel repose toute l'activité pédagogique. Il gère l'arborescence complexe d'un établissement : du campus physique aux départements, jusqu'au découpage précis des classes et sous-groupes de TP. Il orchestre également le **parcours historique des etudiants**, permettant de savoir dans quelle classe un élève se trouvait il y a 3 ans et quel était son programme de modules et de matières à ce moment précis.

---

## 🚀 Points Clés de Gestion
- **Hiérarchie Campus** : Organisation multi-établissements et multi-départements (Filières).
- **Cartographie Pédagogique** : Définition des Unités d'Enseignement (Modules) et de leurs matières constitutives.
- **Paramétrage des Crédits** : Gestion des points ECTS et des coefficients (CC/Examen) par matière.
- **Suivi des Inscriptions** : Gestion des flux d'élèves par année académique avec archivage automatique du parcours.

---

## 🗂️ Modèle de Données (Entités)
- **Establishment / Department** : Les racines de l'organisation.
- **Classe** : Regroupe les etudiants pour une année donnée (Type: `PRINCIPALE` ou `GROUPE`).
- **Module (UE)** : Unité d'enseignement regroupant des thématiques de cours.
- **Subject (EC)** : La matière de base avec ses volumes horaires (CM, TD, TP).
- **Enrollment** : Le lien historique entre un etudiant, une Classe et une Année scolaire.

---

## 📡 Spécifications des APIs (ADMIN)

- `POST /api/academique/classes` : Définir une promotion ou un niveau.
- `POST /api/academique/modules` : Créer un module pour une classe et un semestre.
- `POST /api/academique/inscrire` : Action d'inscrire physiquement un élève.
- `GET /api/academique/etudiant/{id}/historique` : Voir toute la carrière académique de l'élève.

---

## 🎓 Logique d'Organisation
1. **Modules et Crédits** : Chaque module possède un total de crédits qui est la somme des crédits des matières qui le composent.
2. **Coefficients** : Le système applique par défaut **40% pour le Contrôle Continu** et **60% pour l'Examen**, modifiable à la création de la matière.
3. **Année Académique** : Chaque inscription est scellée par une année (ex: 2024-2025) pour garantir l'intégrité des rapports futurs.
