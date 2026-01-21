# 🎓 SchoolGest App - Documentation Technique

Bienvenue dans la documentation officielle de **SchoolGest App**, un ERP de gestion scolaire complet, robuste et moderne basé sur Spring Boot 3.

## 🏗️ Architecture Globale
Le projet suit une architecture modulaire par fonctionnalités pour une meilleure maintenance et scalabilité.

### Technologies Clés :
- **Backend** : Spring Boot 3.4
- **Base de données** : PostgreSQL
- **Sécurité** : Spring Security + JWT (JSON Web Tokens)
- **Stockage Cloud** : Cloudinary (Gestion des fichiers physiques)
- **Documentation API** : Format RESTful avec contrôleurs segmentés

---

## 📂 Sommaire des Modules
1. [Authentification & Utilisateurs](./docs/auth_module.md)
2. [Structure Académique](./docs/academic_module.md)
3. [Emploi du Temps](./docs/schedule_module.md)
4. [Présences & Assiduité](./docs/attendance_module.md)
5. [Évaluations & Bulletins](./docs/grading_module.md)
6. [Devoirs & Ressources](./docs/tasks_resources_module.md)
7. [Communications & Notifications](./docs/communication_module.md)
8. [Administration & Système](./docs/admin_module.md)

---

## 🚀 Installation & Configuration
Consultez le fichier `application.properties` pour configurer :
- La base de données PostgreSQL
- Les identifiants **Cloudinary** pour le stockage
- Les paramètres d'authentification (JWT, etc.)
- chien vers

---

## 🛡️ Sécurité
L'application est sécurisée avec Spring Security et JWT. Les utilisateurs doivent se connecter pour accéder aux différentes fonctionnalités.

### Token JWT
- **Expiration** : 1 heure
- **Génération** : Utilisez le service `AuthService` pour générer un token valide.

---

## 📝 Documentation API
L'application est documentée avec Swagger/OpenAPI. Vous pouvez tester les APIs en temps réel via l'interface Swagger.

### Accès à la Documentation
## 📚 Documentation Interactive (Swagger)
L'application intègre **Swagger/OpenAPI**, permettant de tester les APIs en temps réel.

- **URL de la Doc** : `http://localhost:8087/swagger-ui.html`
- **Authentification** : Utilisez le bouton "Authorize" en haut à droite et collez votre token JWT (format : `Bearer <votre_token>`).
