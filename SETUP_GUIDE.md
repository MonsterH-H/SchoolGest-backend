# 🚀 Guide de Demarrage de SchoolGest App

Ce document decrit l'installation, la configuration et l'execution de l'application.

## 📋 Prerequis
- **Java JDK 23**
- **Maven 3.6+**
- **PostgreSQL 13+**
- **Git**
- **IDE** (IntelliJ IDEA, Eclipse, VSCode)

## 🛠️ Installation

### 1. Cloner le depot
```bash
cd c:/SpringBoot/Dev
```

### 2. Configurer la base de donnees
Creez une base PostgreSQL nommee `gestschool_db`.

### 3. Configurer l'application
Renseignez les variables dans `application.properties` (ou via variables d'environnement). Voir `docs/CONFIGURATION.md` et `.env.example`.

Extrait :
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/gestschool_db
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

jwt.secret=${JWT_SECRET}
cloudinary.cloud_name=${CLOUDINARY_CLOUD_NAME}
cloudinary.api_key=${CLOUDINARY_API_KEY}
cloudinary.api_secret=${CLOUDINARY_API_SECRET}
```

### 4. Installer les dependances
```bash
mvn clean install
```

### 5. Demarrer l'application
```bash
mvn spring-boot:run
```

Acces : `http://localhost:8087`

## 📚 Documentation API
- UI Swagger : `http://localhost:8087/swagger-ui.html`
- OpenAPI : `http://localhost:8087/v3/api-docs`

## 🧪 Tests
```bash
mvn test
```

## 🛠️ Deploiement
Voir `docs/DEPLOYMENT.md`.

## 📞 Support
Ouvrir une issue ou contacter l'equipe de developpement.
