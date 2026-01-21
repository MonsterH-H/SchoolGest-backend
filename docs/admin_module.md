# ⚙️ Module Administration & Système

### 📝 Résumé des Capacités
Ce module est la **tour de contrôle** de SchoolGest. Il offre une vision à 360° sur la santé et l'activité de l'établissement. Il permet aux administrateurs de piloter l'école via des indicateurs de performance (KPIs) et de surveiller l'intégrité technique du serveur. C'est également ici que sont gérés les **journaux d'audit**, assurant que chaque action sensible reste tracée. Enfin, il gère la maintenance du stockage cloud pour assurer la pérennité des documents administratifs.

---

## 🚀 Points Clés de Gestion
- **Pilotage par la Donnée** : Dashboard consolidé (Près de 10 indicateurs clés).
- **Analyse de l'Assiduité Globale** : Calcul du taux de présence à l'échelle de tout l'établissement.
- **Audit & Sécurité** : Historisation de toutes les actions pour prévenir les fraudes.
- **Maintenance Technique** : Monitoring système et santé de la base de données.

---

## 🏗️ Services & Utilitaires
- **AdminDashboardService** : Agrège les données pour le pilotage.
- **SystemLog** : Structure de l'historique d'audit.
- **FileUploadService** : Le pont vers Cloudinary utilisé par tous les autres modules.

---

## 📡 Spécifications des APIs (ADMIN)

- `GET /api/admin/dashboard/stats` : Le "Pulse" de l'école (Nombres d'élèves, profs, taux de présence).
- `GET /api/admin/system/status` : État de santé et version du serveur.
- `POST /api/stockage/upload` : Outil de secours pour l'upload de fichiers libres.

---

## 📁 Organisation du Stockage Cloud
Le module administre la hiérarchie Cloudinary :
- `/avatars` (Utilisateurs)
- `/devoirs` et `/soumissions` (Pédagogie)
- `/cours` (Ressources)
- `/justificatifs` (Discipline)
- `/messages` (Communication)
