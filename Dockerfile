# Étape de build
FROM maven:3.8.7-eclipse-temurin-17 AS build
WORKDIR /app

# Copier uniquement le pom.xml d'abord pour optimiser le cache des dépendances
COPY pom.xml .
RUN mvn dependency:go-offline

# Copier le reste du code source
COPY src ./src

# Construire l'application
RUN mvn clean package -DskipTests

# Étape de production
FROM openjdk:17-jdk-slim
WORKDIR /app

# Variables d'environnement minimales
ENV PORT=8080

# Copier le JAR depuis l'étape de build
COPY --from=build /app/target/*.jar app.jar

# Exposer le port
EXPOSE ${PORT}

# Commande pour démarrer l'application
CMD ["java", "-jar", "-Dserver.port=${PORT}", "app.jar"]
