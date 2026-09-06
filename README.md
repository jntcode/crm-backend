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

- JWT-based authentication (login, register, Google OAuth)
- User-scoped data — each user sees only their own records
- Full CRUD for 7 domains: Customers, Leads, Companies, Tasks, Activities, Emails, Dashboard
- Role-based security with stateless session management
- Seed data via `data.sql` for demo purposes
- CORS configured for cross-origin frontend access
- Production profile with PostgreSQL support

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

### Default user

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

### Protected (requires `Authorization: Bearer <token>`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/dashboard/stats` | Dashboard statistics |
| GET | `/api/dashboard/pipeline-breakdown` | Pipeline counts by status |
| GET/POST | `/api/customers` | List / create contacts |
| PUT/DELETE | `/api/customers/{id}` | Update / delete contact |
| GET/POST | `/api/leads` | List / create leads |
| PUT/DELETE | `/api/leads/{id}` | Update / delete lead |
| GET/POST | `/api/companies` | List / create companies |
| PUT/DELETE | `/api/companies/{id}` | Update / delete company |
| GET/POST | `/api/tasks` | List / create tasks |
| PUT/DELETE | `/api/tasks/{id}` | Update / delete task |
| GET/POST | `/api/activities` | List / create activities |
| DELETE | `/api/activities/{id}` | Delete activity |
| GET | `/api/emails` | List emails |
| POST | `/api/emails` | Send email |
| DELETE | `/api/emails/{id}` | Delete email |

## Project Structure

```
src/main/java/com/crm/backend/
├── CrmBackendApplication.java
├── auth/           # Authentication: controller, service, JWT, user entity
├── customer/       # Customer CRUD: controller, service, repository, entity
├── lead/           # Lead CRUD + status enum
├── company/        # Company CRUD
├── task/           # Task CRUD + priority/status
├── activity/       # Activity CRUD + type enum
├── email/          # Email CRUD
├── dashboard/      # Dashboard stats + pipeline breakdown
└── security/       # SecurityConfig, JwtAuthenticationFilter
```

## Environment Variables

| Variable | Required | Description |
|----------|----------|-------------|
| `SPRING_PROFILES_ACTIVE` | No | Set to `prod` for PostgreSQL |
| `DATABASE_URL` | In production | PostgreSQL connection string |
| `JWT_SECRET` | Recommended | Secret key for JWT signing |
| `GOOGLE_CLIENT_ID` | Optional | Google OAuth 2.0 client ID |

## Deployment

### Docker

```bash
docker build -t crm-backend .
docker run -p 8080:8080 crm-backend
```

### Render

Connected via GitHub. Auto-deploys on push to `master`.

## Author

**Jhonata Rusaffa** — [GitHub](https://github.com/jntcode) | [LinkedIn](https://www.linkedin.com/in/jhonata-silva-181675373/)
