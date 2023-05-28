# Stage 1: Build Spring Boot application
FROM maven:3.8.4-openjdk-11 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src/ /app/src/
RUN mvn package -DskipTests

# Stage 2: Run Application in Spring Boot
FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=builder /app/target/primary-customer-base-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "primary-customer-base-0.0.1-SNAPSHOT.jar"]