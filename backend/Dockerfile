# Multi-stage build for the Spring Boot API.
# Runs identically on any Docker host, including Render's free web service plan.
# Docs: https://render.com/docs/docker
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
# Cache dependencies first for faster, more predictable rebuilds.
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
# Tests are executed separately (mvn test); the deploy image only packages the jar.
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# Render injects PORT; application.yml falls back to 8080.
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
