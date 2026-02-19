# Securite

## Authentification
- JWT pour les access tokens
- Refresh token en base (duree 7 jours) pour renouveler l'access token
- Roles : `ADMIN`, `ENSEIGNANT`, `ETUDIANT`

## Verrouillage de compte
- 5 tentatives de connexion echouees
- Verrouillage 15 minutes

## Activation et audit
- Un compte est cree **inactif** par defaut
- Journalisation via `SystemLog`

## Reinitialisation de mot de passe
- Endpoint `POST /api/auth/forgot-password`
- Token de reinitialisation avec expiration (voir `PasswordResetService`)

## Bonnes pratiques recommandées
- Ne jamais versionner de secrets
- Utiliser un gestionnaire de secrets pour la prod
- Forcer HTTPS via reverse proxy
- Limiter CORS aux origines strictement necessaires
- Rotation reguliere des cles JWT
