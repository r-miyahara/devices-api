# Devices API — Clean Architecture

API to manage **Devices** following **Clean Architecture + SOLID + Clean Code**.  
Implements CRUD + filters, domain rules, persistence on PostgreSQL (non in-memory), OpenAPI docs, tests, and containerization.

## Scope (from challenge)
- **Device**: `id`, `name`, `brand`, `state (available | in-use | inactive)`, `creationTime`
- **Features**: create; full/partial update; get by id; list all; list by brand; list by state; delete
- **Domain rules**: `creationTime` **cannot change**; **name/brand cannot change if in-use**; **in-use cannot be deleted**
- **Acceptance**: compiles/runs; reasonable tests; documented API; **DB not in-memory**; containerized; README

## Tech
- Java 21, Maven 3.9+
- Spring Boot 3.3.x (web, validation, actuator)
- springdoc-openapi (Swagger UI)
- PostgreSQL + Flyway (migrations)
- Tests: unit + web slice + JPA slice
- Docker/Docker Compose

## Project layout (multi-module)
```
devices-domain/               # domain (models, enums, ports)
devices-usecase/              # use cases / application services
devices-adapter-web/          # REST controllers, DTOs, mappers, handlers
devices-adapter-persistence/  # JPA entities/repositories, adapters, migrations
devices-boot/                 # Spring Boot app (wiring/config)
```

> If you are on the single-module flavor, paths become `src/main/java/dev/roberto/devices/...` and `src/main/resources/db/migration`.

## How to run (dev)
```bash
# Build
mvn -q -DskipTests -pl devices-boot -am package

# Start Postgres
docker compose up -d db

# Run the app
java -jar devices-boot/target/devices-boot-0.1.0-SNAPSHOT.jar
```

Docs:
- Swagger UI → `http://localhost:8080/docs`
- OpenAPI JSON → `http://localhost:8080/v3/api-docs`

## How to run (containers)
```bash
docker compose up --build
```

## Configuration (env)
| Var | Default | Purpose |
|-----|---------|---------|
| `DB_URL` | `jdbc:postgresql://localhost:5432/devices` | JDBC URL |
| `DB_USER` | `devices` | DB user |
| `DB_PASSWORD` | `devices` | DB password |
| `SPRING_PROFILES_ACTIVE` | *(empty / set by compose)* | Active profile |

## API (quick examples)

**Create**
```bash
curl -i -X POST http://localhost:8080/devices   -H 'Content-Type: application/json'   -d '{"name":"WS-01","brand":"Lenovo","state":"AVAILABLE"}'
```

**Get by ID**
```bash
curl -i http://localhost:8080/devices/{id}
```

**List**
```bash
curl -s http://localhost:8080/devices
curl -s "http://localhost:8080/devices?brand=Lenovo"
curl -s "http://localhost:8080/devices?state=available"
```

**Update (PUT)**
```bash
curl -i -X PUT http://localhost:8080/devices/{id}   -H 'Content-Type: application/json'   -d '{"name":"WS-02","brand":"Lenovo","state":"INACTIVE"}'
```

**Patch**
```bash
curl -i -X PATCH http://localhost:8080/devices/{id}   -H 'Content-Type: application/json'   -d '{"brand":"HP"}'
```

**Delete**
```bash
curl -i -X DELETE http://localhost:8080/devices/{id}
```

### Domain rules (how to verify)
- `creationTime` never changes across updates.  
- With `state=in-use`, attempts to change `name` or `brand` return an error (422).  
- Deleting a device in `in-use` state returns an error (422).

## Tests & coverage
```bash
mvn test
# (if JaCoCo enabled) open target/site/jacoco/index.html
```

## Notes
- Uses PostgreSQL (not in-memory) to satisfy the requirement.
- Containerization provided via Dockerfile + docker-compose.
- Commits are granular and documented for traceability.
