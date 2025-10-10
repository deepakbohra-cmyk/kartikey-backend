# ----------------------------
# Stage 1: Build with Maven + JDK 21
# ----------------------------
FROM maven:3.9.4-eclipse-temurin-21 AS build

WORKDIR /app

# Copy Maven config and source code
COPY pom.xml .
COPY src ./src

# Build the JAR without running tests
RUN mvn clean package -DskipTests

# ----------------------------
# Stage 2: Run Spring Boot app
# ----------------------------
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/target/kartikey-0.0.1-SNAPSHOT.jar app.jar

# Expose Spring Boot port
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
