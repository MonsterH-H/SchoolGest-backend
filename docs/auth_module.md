# 🔐 Module Authentification & Utilisateurs

### 📝 Résumé des Capacités
Ce module constitue le **cerveau de sécurité** et la **gestion d'identité** de l'application. Il ne se contente pas de connecter les utilisateurs ; il gère l'ensemble du cycle de vie des comptes, de l'inscription multi-profils (etudiant, Profil, Admin) à la sécurisation avancée contre les intrusions. Il assure que chaque individu dispose d'un espace personnel personnalisé avec sa propre photo de profil et ses préférences, tout en maintenant une trace d'audit rigoureuse de chaque action sensible.

---

## 🚀 Points Clés de Gestion
- **Identités Multi-Rôles** : Gestion différenciée des accès pour les Administrateurs, les Enseignants et les etudiants.
- **Sessions Sécurisées** : Utilisation du standard JWT avec système de double token (Access + Refresh) pour allier sécurité et confort d'utilisation.
- **Protection Anti-Brute Force** : Surveillance en temps réel des tentatives de connexion avec verrouillage automatique temporaire.
- **Identité Visuelle** : Intégration cloud pour les avatars (photos de profil) des membres.

---

## 🗂️ Modèle de Données (Entités)
### User (L'Utilisateur Global)
- `username` : Identifiant unique de connexion.
- `email` : Adresse de contact et de récupération.
- `role` : Type d'utilisateur (`ADMIN`, `ENSEIGNANT`, `ETUDIANT`).
- `avatarUrl` : Lien vers la photo hébergée sur Cloudinary.
- `active` : Statut du compte (permet de bannir/désactiver un utilisateur).

---

## 📡 Spécifications des APIs

| Méthode | Endpoint | Description | Portée |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Création de compte (JSON + Photo optionnelle) | Public |
| `POST` | `/api/auth/login` | Authentification et remise des tokens | Public |
| `POST` | `/api/auth/refresh` | Renouvellement de l'accès expiré | Authentifié |
| `PUT` | `/api/auth/profile` | Mise à jour (Nom, Prénom, Tél, Photo) | Propriétaire |
| `POST` | `/api/auth/forgot-password` | Envoi du token de récupération par mail | Public |

---

## 🛡️ Logique & Règles de Sécurité
1. **Échecs de connexion** : Après **5 tentatives infructueuses**, le compte est gelé pendant **15 minutes**.
2. **Audit** : Chaque connexion réussie met à jour le champ `lastLogin` pour un suivi de l'activité.
3. **Mots de passe** : Tous les secrets sont hachés avec l'algorithme fort **BCrypt**.
