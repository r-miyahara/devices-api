# Devices API — Clean Architecture Skeleton (First Commit)

This repository is a **baseline skeleton** to implement the 1st step of the challenge:
only **configs, libs, structure, and containerization** — no business code yet.

> Next commits will add domain objects, communication layers (web/persistence), use cases, unit tests, and refactors.

## Stack & Rationale
- **Java 21**, **Maven 3.9+**
- **Spring Boot 3.3.x** for production-ready web app, Actuator, and easy containerization
- **springdoc-openapi** to expose API docs at `/docs`
- **PostgreSQL + Flyway** (non in-memory DB requirement)
- **Multi-module Maven** layout for Clean Architecture boundaries

## Modules
- `devices-domain` — Pure domain models and ports (no framework dependency)
- `devices-usecase` — Application services & use cases (depends on `domain`)
- `devices-adapter-web` — REST controllers, DTO mappers, exception handlers
- `devices-adapter-persistence` — JPA repositories & entities, migrations
- `devices-boot` — Spring Boot launcher (depends on adapters + usecase)

## How to Run (dev)
```bash
# From repository root
mvn -q -DskipTests -pl devices-boot -am package

# Start only Postgres (if you want to run boot locally)
docker compose up -d db

# Run the app locally
java -jar devices-boot/target/devices-boot-0.1.0-SNAPSHOT.jar
```

## How to Run (containers)
```bash
# Build and run app + db
docker compose up --build
```

Once the REST layer is implemented, API docs will be at:
- Swagger UI → `http://localhost:8080/docs`
- OpenAPI JSON → `http://localhost:8080/v3/api-docs`

## Notes
- This skeleton intentionally contains **no domain models or endpoints** yet.
- Next commits will be small and well-commented to match the **granular commits** requirement.
