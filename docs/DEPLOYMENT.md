# Deploiement

## Build
```bash
mvn clean package
```

## Run (jar)
```bash
java -jar target/schoolgestapp-0.0.1-SNAPSHOT.jar
```

## Docker
```bash
docker build -t schoolgest-app .
docker run -p 8087:8087 --env-file .env schoolgest-app
```

Astuce : partez de `.env.example` pour creer votre `.env`.

## Recommandations prod
- Definir `SPRING_PROFILES_ACTIVE=prod`
- Utiliser un reverse proxy (TLS)
- Exposer uniquement le port necessaire
- Configurer la base avec sauvegardes et retention
