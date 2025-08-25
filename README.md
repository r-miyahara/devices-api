# Devices API — Clean Architecture Skeleton (First Commit)

Baseline skeleton to implement the **first step** of the challenge: project wiring, libraries, structure, and containerization — **no business code yet**.

Future commits will add domain objects, communication layers (web/persistence), use cases, unit tests, and refactors. All changes will be delivered as **small, granular commits** with clear messages.

---

## Status

- ✅ Build, run, and containerization ready  
- ✅ Spring Boot app starter (no endpoints yet)  
- ✅ PostgreSQL + Flyway pre-wired (migrations folder ready)  
- ⏳ Domain, Use Cases, Web & Persistence layers (next commits)

---

## Tech Stack & Rationale

- **Java 21**, **Maven 3.9+**
- **Spring Boot 3.3.x** — production-ready web runtime, Actuator, easy containerization
- **springdoc-openapi** — API docs at `/docs` (will appear once the REST layer exists)
- **PostgreSQL + Flyway** — fulfills the “non in-memory DB” requirement and provides versioned migrations
- **Clean Architecture** — strict boundaries via modules

---

## Project Layout (Multi-Module)

```
devices-domain/               # Pure domain models and ports (no framework)
devices-usecase/              # Application services & use cases (depends on domain)
devices-adapter-web/          # REST controllers, DTO mappers, exception handlers
devices-adapter-persistence/  # JPA repositories & entities, Flyway migrations
devices-boot/                 # Spring Boot launcher (depends on adapters + usecase)
```

> The app composes at the **boot** module, keeping the domain and use cases framework-agnostic.

---

## How to Run (Dev)

From repository root:

```bash
# Build the app (without tests for now)
mvn -q -DskipTests -pl devices-boot -am package

# Start only PostgreSQL (so you can run the app locally)
docker compose up -d db

# Run the application
java -jar devices-boot/target/devices-boot-0.1.0-SNAPSHOT.jar
```

Once the REST layer is implemented, API docs will be available at:

- Swagger UI → `http://localhost:8080/docs`  
- OpenAPI JSON → `http://localhost:8080/v3/api-docs`

---

## How to Run (Containers)

```bash
# Build and run app + db
docker compose up --build
```

> By default the app listens on **:8080** and the database on **:5432**.

---

## Configuration

Environment variables (used by Spring properties):

| Env Var         | Default                                      | Purpose                    |
|-----------------|----------------------------------------------|----------------------------|
| `DB_URL`        | `jdbc:postgresql://localhost:5432/devices`   | JDBC URL                   |
| `DB_USER`       | `devices`                                     | DB username                |
| `DB_PASSWORD`   | `devices`                                     | DB password                |
| `SPRING_PROFILES_ACTIVE` | *(empty / set by compose)*          | Active Spring profile      |

Flyway looks for SQL migrations in:  
`devices-adapter-persistence/src/main/resources/db/migration/`

> First commit ships without migrations on purpose. Flyway will simply run with 0 migrations. Next commits will add `V001__...` and subsequent files.

---

## Coding Standards

- **SOLID / Clean Architecture / Clean Code**
- Domain and use cases are **framework-free**
- Adapters (web/persistence) are thin and replaceable
- Small, focused commits with clear messages and “how to validate” notes

---

## Next Commits (Plan)

1. **domain** — `Device`, `DeviceState`, `TimeProvider`, repository ports  
2. **usecase** — `DeviceService` (create/get/list/filter/put/patch/delete + domain rules)  
3. **web** — controllers, DTOs, mappers, validation, error handling, OpenAPI annotations  
4. **persistence** — JPA entities, Spring Data repositories, adapters, Flyway `V001__...`  
5. **boot/config** — final wiring and OpenAPI metadata  
6. **tests** — unit tests (use cases) and slice tests (web/JPA)  
7. **hardening** — ETag/If-None-Match/If-Match, Idempotency-Key, pagination, structured logs, CORS, payload limits  
8. **docs** — README updates and usage examples

---

## Notes

- This first commit intentionally contains **no domain models or endpoints**.
- The app builds and starts to validate infrastructure and containerization early.
- Keep commits **granular** to satisfy the challenge’s “traceable changes” requirement.
