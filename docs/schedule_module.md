# 📅 Module Emploi du Temps

### 📝 Résumé des Capacités
Le module Emploi du Temps est le **régulateur logistique** de l'établissement. Il gère l'occupation des ressources critiques : le **temps** (créneaux), l'**espace** (salles) et les **humains** (enseignants). Sa fonction principale est de garantir un emploi du temps fluide et sans heurts grâce à un moteur de validation intelligent. Il permet également de gérer la réalité mouvante d'une école en offrant des outils de report ou d'annulation de cours en un clic, tout en avertissant les personnes concernées.

---

## 🚀 Points Clés de Gestion
- **Optimisation des Salles** : Suivi des capacités et des équipements (Projecteurs, PCs) pour assigner le bon cours au bon endroit.
- **Grille Horaire Flexible** : Définition personnalisable des créneaux (Matin, Après-midi, Soir).
- **Moteur Anti-Conflit** : Algorithme temps réel vérifiant la disponibilité triple (Prof/Salle/Classe).
- **Gestion des Aléas** : Workflow d'annulation et de reprogrammation automatique des séances.

---

## 🗂️ Modèle de Données (Entités)
- **Salle** : Infrastructure physique.
- **TimeSlot** : Découpage temporel (ex: 8h-10h).
- **Planning** : L'événement réunissant Date + Salle + Prof + Matière + Classe.

---

## 📡 Spécifications des APIs

- `POST /api/emploidutemps/planifier` : Réservation d'une séance de cours.
- `GET /api/emploidutemps/classe/{id}` : Vue "etudiant" filtrée par date.
- `GET /api/emploidutemps/enseignant/{id}` : Vue "Professeur" pour sa semaine.
- `POST /api/emploidutemps/plannings/{id}/reporter` : Déplacement intelligent d'une séance.

---

## 🛡️ Règles Métier (Le Moteur Anti-Conflit)
Avant chaque enregistrement, le système vérifie sur la base de données :
1. **Un professeur** ne peut pas être à deux endroits à la fois.
2. **Une salle** ne peut pas accueillir deux classes différentes simultanément.
3. **Une classe** ne peut pas avoir deux matières programmées au même moment.
*Si l'une de ces conditions n'est pas remplie, le système bloque l'action et renvoie une erreur explicative.*
