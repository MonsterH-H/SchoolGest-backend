# Configuration

Ce projet utilise des proprietes Spring (fichier `application.properties`) et peut etre configure via variables d'environnement.

## Variables obligatoires
- `DB_USERNAME` / `DB_PASSWORD` : acces base PostgreSQL
- `JWT_SECRET` : cle JWT (64 caracteres min recommends)
- `CLOUDINARY_CLOUD_NAME`
- `CLOUDINARY_API_KEY`
- `CLOUDINARY_API_SECRET`
- `MAIL_USERNAME`
- `MAIL_PASSWORD`
- `MAIL_FROM`

## Variables recommandees
- `SPRING_DATASOURCE_URL` : ex. `jdbc:postgresql://localhost:5432/gestschool_db`
- `JWT_EXPIRATION_MS` : duree access token (ms). Par defaut: `86400000`
- `APPLICATION_FRONTEND_URL` : URL front (CORS)
- `APPLICATION_CORS_ALLOWED_ORIGINS` : liste separee par virgules

## Exemple local (PowerShell)
```powershell
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="password"
$env:JWT_SECRET="CHANGE_ME_64_CHARS_MINIMUM"
$env:CLOUDINARY_CLOUD_NAME="your_cloud"
$env:CLOUDINARY_API_KEY="your_key"
$env:CLOUDINARY_API_SECRET="your_secret"
$env:MAIL_USERNAME="resend"
$env:MAIL_PASSWORD="re_xxx"
$env:MAIL_FROM="onboarding@resend.dev"
```

## Fichier d'exemple
Un exemple est fourni : `.env.example`.
## Notes
- Les valeurs dans `src/main/resources/application.properties` sont des placeholders.
- Pour un profil local, vous pouvez aussi creer `application-local.properties` et lancer avec `SPRING_PROFILES_ACTIVE=local`.
