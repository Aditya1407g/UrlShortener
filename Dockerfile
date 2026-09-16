# ============================================================
# Stage 1: Builder — compiles the JAR using Maven
# ============================================================
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

# Copy Maven wrapper and pom first — these change rarely
# By copying them separately, Docker caches the dependency-download layer
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (cached unless pom.xml changes)
RUN ./mvnw dependency:go-offline -B

# Now copy source and build
COPY src src
RUN ./mvnw clean package -DskipTests -B

# ============================================================
# Stage 2: Runtime — minimal image with just the JAR
# ============================================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the JAR from builder stage — only artifact that ships
COPY --from=builder /build/target/*.jar app.jar

# Document the port the container listens on (informational only)
EXPOSE 8080

# The command run when a container starts
ENTRYPOINT ["java", "-jar", "/app/app.jar"]