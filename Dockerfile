# Stage 1: Build & Package the Application
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /build

# Leverage Docker cache layer for dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build jar
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Minimal Production Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Run as non-privileged system user for security
RUN addgroup -S clinic && adduser -S clinicuser -G clinic
USER clinicuser

# Copy compiled artifact from builder stage
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]