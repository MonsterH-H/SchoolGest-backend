# API Reference

La specification OpenAPI est la source de verite :
- UI Swagger : `http://localhost:8087/swagger-ui.html`
- Spec JSON : `http://localhost:8087/v3/api-docs`

## Conventions
- Base URL : `http://localhost:8087`
- Auth header : `Authorization: Bearer <token>`
- Format d'erreur (GlobalExceptionHandler) :
```json
{
  "status": "ERREUR_HTTP",
  "message": "Description",
  "timestamp": 1730000000000
}
```

## Auth
- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `POST /api/auth/forgot-password`
- `PUT /api/auth/profile`

## Modules cles (extraits)
- Academique : `/api/academique/*`
- Emploi du temps : `/api/emploidutemps/*`
- Presences : `/api/presences/*`
- Evaluations/Bulletins : `/api/evaluations/*`, `/api/bulletins/*`
- Travaux : `/api/travaux/*`
- Communications : `/api/communications/*`
- Admin : `/api/admin/*`

## Export de la spec
```bash
curl http://localhost:8087/v3/api-docs > openapi.json
```
