# 🎓 SchoolGest App - Documentation Technique

Documentation technique destinee aux equipes d'integration et d'exploitation.

## 🏗️ Architecture
- **Style** : architecture modulaire par fonctionnalites
- **Backend** : Spring Boot 3.4.1 (Java 23)
- **Base de donnees** : PostgreSQL
- **Securite** : Spring Security + JWT + BCrypt
- **Stockage** : Cloudinary

## 📚 Sommaire
1. Modules metier : `docs/*.md`
2. Configuration : `docs/CONFIGURATION.md`
3. Securite : `docs/SECURITY.md`
4. API : `docs/API_REFERENCE.md`
5. Deploiement : `docs/DEPLOYMENT.md`
6. Operations : `docs/OPERATIONS.md`

## 📊 Diagrammes
- Architecture : `docs/architecture_diagrams.puml`
- Schema de donnees : `docs/database_schema.puml`
- Sequences : `docs/sequence_diagrams.puml`

## 🚀 Installation & Configuration
Voir `SETUP_GUIDE.md` et `docs/CONFIGURATION.md`.

## 📝 API
Swagger/OpenAPI :
- UI : `http://localhost:8087/swagger-ui.html`
- Spec : `http://localhost:8087/v3/api-docs`

## 🛡️ Securite
Les mecanismes d'authentification, refresh token, verrouillage et audit sont decrits dans `docs/SECURITY.md`.
