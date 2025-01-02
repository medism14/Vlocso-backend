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

# Configuration pour Render.com
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_ADDRESS=0.0.0.0
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# Le port sera fourni par Render via la variable d'environnement PORT
ENV PORT=${PORT:-8080}
EXPOSE ${PORT}

# Healthcheck pour Render
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:${PORT}/ || exit 1

# Script de démarrage optimisé pour Render
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar -Dserver.address=${SERVER_ADDRESS} -Dserver.port=${PORT} app.jar"]
