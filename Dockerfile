# Étape de build
FROM maven:3.8.7-eclipse-temurin-17 AS build
WORKDIR /app

# Copier uniquement le pom.xml d'abord
COPY pom.xml .
RUN mvn dependency:go-offline

# Copier le reste du code source
COPY src ./src

# Construire l'application
RUN mvn clean package -DskipTests

# Étape de production
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copier le JAR depuis l'étape de build
COPY --from=build /app/target/*.jar app.jar

# Exposer le port dynamiquement
ENV PORT=8080
EXPOSE ${PORT}

# Script de démarrage
ENTRYPOINT ["sh", "-c", "java -jar -Dserver.port=${PORT} app.jar"]
