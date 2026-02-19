# Operations

## Logs
- Centraliser les logs applicatifs (ELK, Grafana Loki, etc.)
- Surveiller les erreurs 5xx et taux d'authentification echouee

## Monitoring
- Verifier la disponibilite via endpoints exposes (ex: `/api/admin/system/status` si accessible)
- Ajouter des probes (liveness/readiness) via le reverse proxy

## Sauvegardes
- Backups quotidiens de la base PostgreSQL
- Retention et restauration testee

## Audit
- Les actions sensibles sont journalisees dans `SystemLog`
- Prevoir des exports periodiques pour conformite
