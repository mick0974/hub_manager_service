# Build
FROM maven:3.8.8-eclipse-temurin-21-alpine AS build

WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN mvn clean package

# Rub
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app
COPY --from=build /build/target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]