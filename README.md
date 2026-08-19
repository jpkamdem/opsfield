# Opsfield
Cette app est une plateforme de gestion de prestataires en petites équipes chaperonnées par des managers.

### Stack
- Angular (20)
- Java Spring Boot (4.1.0)
- PostgreSQL (18.4)
- Podman (5.7.0)

### Prérequis
Crée `.env` à la racine du projet :
```env
APP_NAME=opsfield
APP_SECRET=5E+qfLII5nfcuFaTLI2xlTedsuUySpqQMcC6oqZSJbU= #openssl rand -base64 32
POSTGRES_DB=${APP_NAME}_db
POSTGRES_USER=nagato
POSTGRES_PASSWORD=hope
DB_HOST=database
DB_PORT=5432
PGADMIN_DEFAULT_EMAIL=nagato@ame.com
PGADMIN_DEFAULT_PASSWORD=${POSTGRES_PASSWORD}
SPRING_DATASOURCE_URL=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${POSTGRES_DB}
```
Modifie `server/opsfield/src/main/resources/application.yaml` :
```yaml
# https://docs.spring.io/spring-boot/appendix/application-properties/index.html#appendix.application-properties.server

logging:
  level:
    org.springframework.security: DEBUG

app:
  secret: ${APP_SECRET:5E+qfLII5nfcuFaTLI2xlTedsuUySpqQMcC6oqZSJbU=}
  duration: 2592000000 # 1 month

server:
  port: 3000

spring:
  application:
    name: ${APP_NAME:opsfield}
  datasource:
    #dev
    url: ${SPRING_DATASOURCE_URL:jdbc:postgresql://127.0.0.1:5432/opsfield_db}
    username: ${POSTGRES_USER:nagato}
    password: ${POSTGRES_PASSWORD:hope}

  jpa:
    show-sql: true
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    open-in-view: false
    hibernate:
      ddl-auto: validate
```
### Roles
- worker (accès à ses propres ressources)
- manager (accès à ses propres ressources et celles des workers affiliés)
- admin (accès à tout)
### Middlewares
- `[...]/middlewares/LoggedInFilter.java` (identification)
- `[...]/middlewares/PermsFilter.java` (authorization)
### API Endpoints
<u>Non protégées</u>
- POST /api/auth/register
- POST /api/auth/login
- POST /api/auth/logout
- GET /health/ping

<u>Protégées</u>
- GET /api/users/ (admin only)
- GET /api/users/{id} (admin, worker associé à l'id, manager gérant le worker associé)
- PUT /api/users/{id} (idem)
- DELETE /api/users/{id} (idem)

### Choix de la méthode d'authentification
<u>Stateless</u> (scalable, JWT, cookies sécurisés)

### Démarrage
`server/dev-launch.sh`