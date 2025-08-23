# ---------- Build stage ----------
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /workspace
COPY pom.xml .
COPY devices-domain/pom.xml devices-domain/pom.xml
COPY devices-usecase/pom.xml devices-usecase/pom.xml
COPY devices-adapter-web/pom.xml devices-adapter-web/pom.xml
COPY devices-adapter-persistence/pom.xml devices-adapter-persistence/pom.xml
COPY devices-boot/pom.xml devices-boot/pom.xml
# Pre-fetch dependencies
RUN mvn -q -DskipTests dependency:go-offline

# Copy sources and build only boot (and its deps)
COPY . .
RUN mvn -q -DskipTests -pl devices-boot -am package

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre
ENV JAVA_OPTS=""
WORKDIR /app
COPY --from=builder /workspace/devices-boot/target/devices-boot-0.1.0-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /app/app.jar" ]
