# --- Build stage ---
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy all necessary files at once
COPY --chmod=755 mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Build the application (this will download dependencies as needed)
RUN ./mvnw -B -DskipTests package

# --- Runtime stage ---
FROM eclipse-temurin:21-jre

LABEL author="dmcet"

WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/target/photo-dump-*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]