# URL Shortener

A production-style URL shortening service built with Spring Boot and PostgreSQL.

**Status:** Layer 1 complete — actively developing Layer 2 (auth, caching, rate limiting).

---

## Demo

![POST /api/shorten example](docs/postman-shorten.png)

---

## Features

-  Shorten long URLs to compact base62 codes
-  Redirect from short URLs to originals (302)
-  Click count tracking
-  Input validation with detailed error responses
-  Global exception handling (404 / 400 / 500)
- Persistent storage via PostgreSQL with auto-schema management (Hibernate DDL).

## Tech Stack

- Java 21
- Spring Boot 4.1
- PostgreSQL 17
- Spring Data JPA / Hibernate
- Maven
- Bean Validation (Hibernate Validator)

---

## API Documentation

| Method | Endpoint | Description | Status Codes |
|---|---|---|---|
| POST | `/api/shorten` | Create a shortened URL | 201, 400 |
| GET | `/{shortCode}` | Redirect to original URL | 302, 404 |

### `POST /api/shorten`

**Request:**
```json
{"url": "https://example.com/very/long/path"}
```

**Response (201 Created):**
```json
{"shortUrl": "http://localhost:8080/abc"}
```

**Validation errors (400 Bad Request):**
```json
{
    "status": 400,
    "error": "Bad Request",
    "message": "Validation failed",
    "details": [{"field": "url", "issue": "URL is required"}],
    "timestamp": "2026-08-15T10:23:45"
}
```

### `GET /{shortCode}`

Redirects (HTTP 302) to the original long URL. Increments click count on each visit.

**Not-found response (404):**
```json
{
    "status": 404,
    "error": "Not Found",
    "message": "Short code not found: abc",
    "timestamp": "2026-08-15T10:23:45"
}
```

---

## Local Setup

**Prerequisites:**
- Java 21
- PostgreSQL 17 running on `localhost:5432`
- A database named `urlshortener`

**Environment variables:**
- `DB_PASSWORD` — your PostgreSQL password

**Run:**
```bash
./mvnw spring-boot:run
```

The app starts on `http://localhost:8080`.

---

## Engineering Decisions

**Base62 encoding for short codes.** Short codes are derived directly from the auto-generated database ID, eliminating collision checks and keeping URLs compact.

**DTOs separate from entities.** Request and response objects are separate classes from the database entity, preventing mass assignment vulnerabilities and decoupling the API contract from the database schema.

**Global exception handling via @ControllerAdvice.** A single class handles all exception-to-response translation, ensuring consistent JSON error format across every endpoint.

**HTTP 302 (temporary) over 301 (permanent) for redirects.** 302 ensures every visit hits the server, allowing accurate click count tracking. 301 would cache the redirect in browsers, breaking analytics.

**Optional<Url> for repository lookups.** Returning `Optional<Url>` instead of nullable references forces callers to handle the "not found" case explicitly, preventing NullPointerExceptions.

---

## Roadmap

**Layer 2 — Production features (in progress):**
- **JWT authentication** — register/login endpoints, user-scoped URL ownership
- **Redis caching** — cache short-code → long-URL lookups for hot links
- **Rate limiting** — per-user throttling on shorten and redirect endpoints
- **Click analytics** — track time, country, referrer per redirect

**Layer 3 — Deployment & operations:**
- **Docker** — containerize the app + Postgres via docker-compose
- **CI/CD** — GitHub Actions for build, test, deploy on push to main
- **Cloud deployment** — free-tier hosting (Render / Railway / cloud VM)
- **Monitoring** — Spring Boot Actuator endpoints