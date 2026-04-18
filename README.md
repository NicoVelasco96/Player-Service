# 🎮 Player Service

Microservice responsible for player registration, authentication via JWT, and Steam account integration. Built with Spring Boot, PostgreSQL, and Spring Security.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [Environment Variables](#environment-variables)
- [API Endpoints](#api-endpoints)
- [Project Structure](#project-structure)

---

## Overview

**Player Service** is part of a larger tournament management platform built with a microservices architecture. This service handles:

- Player registration and authentication
- JWT-based stateless security
- Steam account linking and profile enrichment
- Secure password storage with BCrypt

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 4.0.5 |
| Security | Spring Security + JWT (jjwt 0.13) |
| Database | PostgreSQL (via Railway) |
| ORM | Hibernate / Spring Data JPA |
| HTTP Client | WebClient (WebFlux) |
| Documentation | SpringDoc OpenAPI (Swagger UI) |
| Build Tool | Gradle |
| Utilities | Lombok, Slf4j |

---

## Architecture

This service follows a layered architecture pattern:

```
PlayerController
      │
IPlayerService ──► PlayerService
      │
IPlayerRepository ──► PostgreSQL
      │
SteamAPIClient ──► Steam Web API
```

Security is handled by a stateless JWT filter (`OncePerRequestFilter`) configured in `SecurityConfig`, which intercepts every request and validates the Bearer token before granting access.

---

## Getting Started

### Prerequisites

- Java 17+
- Gradle
- PostgreSQL database (local or cloud)

### Run locally

1. Clone the repository:
```bash
git clone https://github.com/NicoVelasco96/Player-Service.git
cd Player-Service
```

2. Set the required environment variables (see below).

3. Run the application:
```bash
./gradlew bootRun
```

4. Access Swagger UI at:
```
http://localhost:8081/swagger-ui.html
```

---

## Environment Variables

Configure the following variables in your environment or IDE run configuration:

| Variable | Description | Example |
|---|---|---|
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://host:port/db?options=-c%20timezone%3DUTC` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `yourpassword` |
| `JWT_SECRET` | Secret key for JWT signing (min 256 bits) | `supersecretkey...` |
| `STEAM_API_KEY` | Steam Web API key (optional) | `ABC123...` |

> ⚠️ Never commit real credentials to the repository. Always use environment variables.

---

## API Endpoints

Base URL: `http://localhost:8081`

| Method | Endpoint | Auth Required | Description |
|---|---|---|---|
| `POST` | `/api/players/register` | ❌ | Register a new player |
| `POST` | `/api/players/login` | ❌ | Authenticate and receive JWT |
| `GET` | `/api/players/{id}` | ✅ | Get player profile by ID |
| `POST` | `/api/players/{id}/steam` | ✅ | Link a Steam account |

### Authentication

Protected endpoints require a Bearer token in the `Authorization` header:

```
Authorization: Bearer <your_jwt_token>
```

Obtain the token by calling `/api/players/login`.

### Example: Register

```json
POST /api/players/register
{
  "username": "nicol",
  "email": "nicol@example.com",
  "password": "securepassword",
  "steamId": "76561198XXXXXXX"
}
```

### Example: Login

```json
POST /api/players/login
{
  "username": "nicol",
  "password": "securepassword"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzM4NCJ9...",
  "player": {
    "id": 1,
    "username": "nicol",
    "email": "nicol@example.com",
    "steamDisplayName": "NicolV96",
    "steamAvatarUrl": "https://...",
    "status": "ACTIVE"
  }
}
```

---

## Project Structure

```
src/main/java/com/tournament/player/
├── client/
│   └── SteamAPIClient.java         # Steam Web API integration
├── config/
│   ├── AppConfig.java              # WebClient & OpenAPI config
│   └── SecurityConfig.java         # Spring Security + JWT filter
├── controller/
│   └── PlayerController.java       # REST endpoints
├── dto/
│   └── PlayerDTO.java              # Request/Response DTOs
├── model/
│   ├── Player.java                 # JPA Entity
│   └── PlayerStatus.java           # Enum
├── repository/
│   └── IPlayerRepository.java      # Spring Data JPA repository
├── service/
│   ├── IPlayerService.java         # Service interface
│   ├── PlayerService.java          # Business logic
│   └── JWTService.java             # JWT generation & validation
└── PlayerServiceApplication.java
```

---

## License

This project is part of a personal portfolio. Feel free to use it as reference.
