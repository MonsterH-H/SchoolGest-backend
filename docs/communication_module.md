# 💬 Module Communications & Notifications

### 📝 Résumé des Capacités
Le module Communications est le **tissu social et informatif** de SchoolGest. Il assure la cohésion de la communauté scolaire en permettant des échanges directs et sécurisés entre les membres (professeurs, élèves, administrateurs). Sa fonction critique est la **gestion des notifications**, qui agit comme un système nerveux : chaque événement important dans la vie de l'élève (une nouvelle note, un devoir urgent, une absence signalée) est immédiatement notifié pour garantir une réactivité maximale.

---

## 🚀 Points Clés de Gestion
- **Messagerie Privée** : Système d'e-mails interne avec support de pièces jointes.
- **Accusés de Réception** : Suivi précis des dates et heures de lecture des messages.
- **Alertes Multi-Canaux** : Centralisation de toutes les notifications système.
- **Compteur Global de Vigilance** : Calcul en temps réel du nombre d'éléments non lus pour l'interface.

---

## 🗂️ Modèle de Données (Entités)
- **Message** : Courrier interne sécurisé.
- **Notification** : Alerte système éphémère (Types: `INFO`, `SUCCESS`, `WARNING`, `DANGER`).

---

## 📡 Spécifications des APIs

- `POST /api/communications/messages` : Envoyer un message (Support fichiers cloud).
- `GET /api/communications/boite-reception/{userId}` : Liste des derniers messages.
- `GET /api/communications/notifications/{userId}` : Flux d'alertes trié par urgence.
- `GET /api/communications/non-lus/{userId}` : Source pour le badge de notification UI.

---

## 🔔 Événements Déclencheurs (Triggers)
Le système génère des notifications automatiquement pour :
1. Chaque **nouvelle note** publiée par un enseignant.
2. Chaque **devoir** ou ressource mis en ligne pour une classe.
3. Chaque **absence** enregistrée ou alerte disciplinaire.
4. Chaque **bulletin** de notes généré par l'administration.
