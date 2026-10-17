# CRM Backend

Spring Boot 3.4 REST API for a full-featured CRM platform. Powers the [CRM Frontend](https://github.com/jntcode/crm-frontend) deployed at [crm-frontend-six-pearl.vercel.app](https://crm-frontend-six-pearl.vercel.app/).

## Tech Stack

- **Java 21** + **Spring Boot 3.4**
- **Spring Security** — JWT authentication + Google OAuth 2.0
- **Spring Data JPA** + **Hibernate**
- **H2** (development) / **PostgreSQL** (production)
- **Maven** build system
- **Docker** multi-stage build
- Deployed on **Render** (free tier)

## Features

- JWT-based authentication (login, register, Google OAuth, profile/password management)
- User-scoped data — each user sees only their own records; cross-user access answers 404
- Full CRUD for 7 domains: Customers, Leads, Companies, Tasks, Activities, Emails, Dashboard
- Global exception handling — consistent JSON errors (400/401/403/404/409/500), no stack traces
- Bean validation on all write endpoints
- CORS configured via `app.cors.allowed-origins` (no hardcoded origins)
- Production profile with PostgreSQL and fail-fast startup checks
- Dev-only demo account seeded by `DataInitializer` (never active in production)

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+

### Run locally

```bash
# Clone
git clone https://github.com/jntcode/crm-backend.git
cd crm-backend

# Run
mvn spring-boot:run
```

Server starts at `http://localhost:8080`

H2 Console available at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:crmdb`)

### Default user (dev profile only)

Seeded automatically when running with the default `dev` profile. Production
never contains a shared account.

| Email | Password |
|-------|----------|
| admin@crm.com | secret123 |

## API Endpoints

### Auth (public)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/login` | Login with email/password |
| POST | `/api/auth/register` | Register new account |
| POST | `/api/auth/google` | Login with Google ID token |

### Auth (requires `Authorization: Bearer <token>`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| PUT | `/api/auth/profile` | Update name/email (returns fresh token) |
| PUT | `/api/auth/password` | Change password (403 if current password wrong) |

### Protected (requires `Authorization: Bearer <token>`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/dashboard/stats` | Dashboard statistics (incl. won value this month) |
| GET | `/api/dashboard/pipeline-breakdown` | Pipeline counts by status |
| GET | `/api/dashboard/revenue` | Won lead value per month (last 6 months) |
| GET | `/api/customers` | List contacts |
| POST | `/api/customers` | Create contact |
| GET | `/api/customers/{id}` | Read contact (404 if not yours) |
| PUT/DELETE | `/api/customers/{id}` | Update / delete contact |
| GET/POST | `/api/leads` | List / create leads |
| PUT/DELETE | `/api/leads/{id}` | Update / delete lead |
| GET/POST | `/api/companies` | List / create companies |
| PUT/DELETE | `/api/companies/{id}` | Update / delete company |
| GET | `/api/tasks` | List tasks |
| POST | `/api/tasks` | Create task |
| GET | `/api/tasks/customer/{customerId}` | Tasks for one customer |
| GET | `/api/tasks/status/{status}` | Tasks by status |
| PUT/DELETE | `/api/tasks/{id}` | Update / delete task |
| GET | `/api/activities` | List activities |
| POST | `/api/activities` | Create activity |
| GET | `/api/activities/customer/{customerId}` | Activities for one customer |
| DELETE | `/api/activities/{id}` | Delete activity |
| GET | `/api/emails` | List email logs |
| POST | `/api/emails` | Log an email (records history; does not send) |
| GET | `/api/emails/customer/{customerId}` | Emails for one customer |
| GET | `/api/emails/status/{status}` | Emails by status |
| PUT/DELETE | `/api/emails/{id}` | Update status / delete email log |

### Error responses

Errors are JSON: `{"error": "message"}`. Validation failures add a field map:
`{"error": "Validation failed", "fields": {"name": "Name is required"}}`.
Status codes: 400 bad input, 401 missing/invalid token, 403 wrong current
password, 404 not found or not yours, 409 email already registered, 500
unexpected (generic message, details only in server logs).

## Project Structure

```
src/main/java/com/crm/backend/
├── CrmBackendApplication.java
├── auth/           # Authentication: controller, service, JWT, user entity
├── common/         # GlobalExceptionHandler + shared exceptions
├── config/         # Prod fail-fast checks, dev DataInitializer
├── customer/       # Customer CRUD: controller, service, repository, entity
├── lead/           # Lead CRUD + status enum + monthly revenue aggregation
├── company/        # Company CRUD
├── task/           # Task CRUD + priority/status
├── activity/       # Activity CRUD + type enum
├── email/          # Email log CRUD (records history; no sending)
├── dashboard/      # Dashboard stats, pipeline breakdown, revenue by month
└── security/       # SecurityConfig, JwtAuthenticationFilter
```

## Testing

```bash
mvn test
```

Covers JWT issuance/validation/failure, auth API flows (register/login/401/409/400),
cross-user isolation (404 on foreign resources), bean validation (400 with field
errors), and dashboard revenue aggregation (per-user, WON-only, real dates).
CI runs `mvn clean verify` plus a Docker image build on every push/PR.

## Environment Variables

| Variable | Required | Description |
|----------|----------|-------------|
| `SPRING_PROFILES_ACTIVE` | No | Defaults to `dev`. Use `prod` for PostgreSQL |
| `JWT_SECRET` | Yes in prod | HS256 signing key, minimum 32 characters |
| `DB_HOST` | Yes in prod | PostgreSQL host (Render binds this automatically) |
| `DB_PORT` | Yes in prod | PostgreSQL port |
| `DB_NAME` | Yes in prod | Database name |
| `DB_USERNAME` | Yes in prod | Database user |
| `DB_PASSWORD` | Yes in prod | Database password |
| `CORS_ALLOWED_ORIGINS` | Yes in prod | Comma-separated allowed origins |
| `GOOGLE_CLIENT_ID` | Optional | Google OAuth 2.0 client ID for `/api/auth/google` |

The `prod` profile refuses to start when database or JWT configuration is
missing — it never falls back to H2.

## Deployment

### Docker

```bash
docker build -t crm-backend .
# prod profile (default in image) — requires DB_* and JWT_SECRET:
docker run -p 8080:8080 \
  -e JWT_SECRET=your-long-random-secret-at-least-32-chars \
  -e DB_HOST=... -e DB_PORT=... -e DB_NAME=... -e DB_USERNAME=... -e DB_PASSWORD=... \
  crm-backend
# or run locally against H2:
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=dev -e JWT_SECRET=your-long-random-secret-at-least-32-chars crm-backend
```

### Render

Connected via GitHub and configured by `render.yaml`: provisions a free
PostgreSQL database, binds `DB_*` automatically, generates `JWT_SECRET`, and
sets `SPRING_PROFILES_ACTIVE=prod` plus the frontend origin in
`CORS_ALLOWED_ORIGINS`.

## Author

**Jhonata Rusaffa** — [GitHub](https://github.com/jntcode) | [LinkedIn](https://www.linkedin.com/in/jhonata-rusaffa/)
