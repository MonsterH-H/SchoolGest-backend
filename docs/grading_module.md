# 📊 Module Évaluations & Bulletins

### 📝 Résumé des Capacités
Ce module est le **centre analytique de la réussite scolaire**. Il gère tout le cycle de la notation, de la saisie brute par l'enseignant au calcul complexe des moyennes semestrielles. Il assure une équité totale en appliquant strictement les pondérations définies (CC vs Examen). Sa finalité est la production de **Bulletins de Notes officiels** qui incluent non seulement les résultats, mais aussi la position relative de l'élève (rang) et sa progression au sein de sa promotion.

---

## 🚀 Points Clés de Gestion
- **Notation Multidimensionnelle** : Gestion des différents types d'évaluations (Projets, Devoirs, Examens).
- **Moteur de Moyennes Pondérées** : Automatisation des calculs à trois niveaux (Matière, Module, Semestre).
- **Gestion Statistique** : Calcul des rangs au sein de la classe pour favoriser l'excellence.
- **Révision et Validation** : Workflow permettant de passer d'une note brouillon (`DRAFT`) à une note officielle et scellée.

---

## 🗂️ Modèle de Données (Entités)
- **Grade** : La note individuelle liée à un sujet et un élève.
- **ReportCard** : Le document final du semestre (Moyenne générale, Rang).
- **ModuleResult / SubjectResult** : Données intermédiaires structurées pour l'affichage détaillé.

---

## 📡 Spécifications des APIs

- `POST /api/evaluations/notes` : Saisie des scores par les professeurs.
- `PUT /api/evaluations/matiere/{id}/publier` : Action de rendre les notes visibles.
- `POST /api/bulletins/generer` : Création du snapshot officiel de réussite.
- `POST /api/bulletins/calculer-rangs` : Algorithme de tri et de classement de la classe.

---

## 🧮 Algorithmes de Calcul
1. **Moyenne EC** : Moyenne des CC multipliée par le coeff CC + Note Examen multipliée par le coeff Exam.
2. **Moyenne UE** : Somme des moyennes EC pondérées par les crédits individuels, divisée par le total des crédits du module.
3. **Passage** : Utilisation des ECTS acquis pour déterminer le statut de réussite.
 Elisa
