# 🎮 Player Service

Escoge tu idioma / Choose your language:

<details open>
<summary><b>🇪🇸 Español</b></summary>

## Resumen

**Player Service** es un microservicio responsable del registro de jugadores, autenticación mediante JWT e integración con cuentas de Steam. Forma parte de una plataforma de gestión de torneos construida con arquitectura de microservicios.

Este servicio se encarga de:
- Registro y autenticación de jugadores
- Seguridad stateless basada en JWT
- Vinculación de cuentas de Steam y enriquecimiento de perfiles
- Almacenamiento seguro de contraseñas con BCrypt

---

## Stack Tecnológico

| Capa | Tecnología |
|---|---|
| Lenguaje | Java 17 |
| Framework | Spring Boot 4.0.5 |
| Seguridad | Spring Security + JWT (jjwt 0.13) |
| Base de Datos | PostgreSQL (vía Railway) |
| ORM | Hibernate / Spring Data JPA |
| Cliente HTTP | WebClient (WebFlux) |
| Documentación | SpringDoc OpenAPI (Swagger UI) |
| Build Tool | Gradle |
| Utilidades | Lombok, Slf4j |

---

## Arquitectura

```
PlayerController
      │
IPlayerService ──► PlayerService
      │
IPlayerRepository ──► PostgreSQL
      │
SteamAPIClient ──► Steam Web API
```

La seguridad es manejada por un filtro JWT stateless (`OncePerRequestFilter`) configurado en `SecurityConfig`, que intercepta cada request y valida el Bearer token antes de conceder acceso.

---

## Primeros Pasos

### Requisitos

- Java 17+
- Gradle
- Base de datos PostgreSQL

### Ejecución local

1. Clonar el repositorio:
```bash
git clone https://github.com/NicoVelasco96/Player-Service.git
cd Player-Service
```

2. Configurar las variables de entorno (ver sección correspondiente).

3. Ejecutar la aplicación:
```bash
./gradlew bootRun
```

4. Acceder a Swagger UI en:
```
http://localhost:8081/api/docs
```

---

## Variables de Entorno

| Variable | Descripción | Ejemplo |
|---|---|---|
| `SPRING_DATASOURCE_URL` | URL JDBC de PostgreSQL | `jdbc:postgresql://host:port/db` |
| `SPRING_DATASOURCE_USERNAME` | Usuario de la base de datos | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de la base de datos | `tupassword` |
| `JWT_SECRET` | Clave secreta para firmar JWT (mín. 256 bits) | `supersecretkey...` |
| `STEAM_API_KEY` | Clave de la Steam Web API (opcional) | `ABC123...` |

> ⚠️ Nunca subas credenciales reales al repositorio. Siempre usá variables de entorno.

---

## Endpoints

URL Base: `http://localhost:8081`

| Método | Endpoint | Auth | Descripción |
|---|---|---|---|
| `POST` | `/api/players/register` | ❌ | Registrar un nuevo jugador |
| `POST` | `/api/players/login` | ❌ | Autenticarse y obtener JWT |
| `GET` | `/api/players/{id}` | ✅ | Obtener perfil por ID |
| `POST` | `/api/players/{id}/steam` | ✅ | Vincular cuenta de Steam |
| `GET` | `/api/players/service-token` | ✅ | Generar token de servicio |

### Ejemplo: Registro

```json
POST /api/players/register
{
  "username": "nicol",
  "email": "nicol@example.com",
  "password": "securepassword",
  "steamId": "76561199383658965"
}
```

### Ejemplo: Login

```json
POST /api/players/login
{
  "username": "nicol",
  "password": "securepassword"
}
```

Respuesta:
```json
{
  "token": "eyJhbGciOiJIUzM4NCJ9...",
  "player": {
    "id": 1,
    "username": "nicol",
    "steamDisplayName": "NicolV96",
    "steamLevel": 5,
    "steamProfileUrl": "https://steamcommunity.com/profiles/...",
    "status": "ACTIVE"
  }
}
```

---

## Estructura del Proyecto

```
src/main/java/com/tournament/player/
├── client/
│   └── SteamAPIClient.java         # Integración con Steam Web API
├── config/
│   ├── AppConfig.java              # Configuración WebClient y OpenAPI
│   └── SecurityConfig.java         # Spring Security + filtro JWT
├── controller/
│   └── PlayerController.java       # Endpoints REST
├── dto/
│   └── PlayerDTO.java              # DTOs de request/response
├── model/
│   ├── Player.java                 # Entidad JPA
│   └── PlayerStatus.java           # Enum de estado
├── repository/
│   └── IPlayerRepository.java      # Repositorio Spring Data JPA
├── service/
│   ├── IPlayerService.java         # Interfaz del servicio
│   ├── PlayerService.java          # Lógica de negocio
│   └── JWTService.java             # Generación y validación de JWT
└── PlayerServiceApplication.java
```

</details>

---

<details>
<summary><b>🇺🇸 English</b></summary>

## Overview

**Player Service** is a microservice responsible for player registration, authentication via JWT, and Steam account integration. Part of a larger tournament management platform built with a microservices architecture.

This service handles:
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
- PostgreSQL database

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
http://localhost:8081/api/docs
```

---

## Environment Variables

| Variable | Description | Example |
|---|---|---|
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://host:port/db` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `yourpassword` |
| `JWT_SECRET` | Secret key for JWT signing (min 256 bits) | `supersecretkey...` |
| `STEAM_API_KEY` | Steam Web API key (optional) | `ABC123...` |

> ⚠️ Never commit real credentials to the repository. Always use environment variables.

---

## API Endpoints

Base URL: `http://localhost:8081`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/api/players/register` | ❌ | Register a new player |
| `POST` | `/api/players/login` | ❌ | Authenticate and receive JWT |
| `GET` | `/api/players/{id}` | ✅ | Get player profile by ID |
| `POST` | `/api/players/{id}/steam` | ✅ | Link a Steam account |
| `GET` | `/api/players/service-token` | ✅ | Generate service token |

### Example: Register

```json
POST /api/players/register
{
  "username": "nicol",
  "email": "nicol@example.com",
  "password": "securepassword",
  "steamId": "76561199383658965"
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
    "steamDisplayName": "NicolV96",
    "steamLevel": 5,
    "steamProfileUrl": "https://steamcommunity.com/profiles/...",
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
│   └── PlayerStatus.java           # Status enum
├── repository/
│   └── IPlayerRepository.java      # Spring Data JPA repository
├── service/
│   ├── IPlayerService.java         # Service interface
│   ├── PlayerService.java          # Business logic
│   └── JWTService.java             # JWT generation & validation
└── PlayerServiceApplication.java
```

</details>

---

## 📜 Licencia / License

Este proyecto es parte de un portafolio personal. / This project is part of a personal portfolio.
