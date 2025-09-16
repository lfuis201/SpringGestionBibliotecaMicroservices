# ----------------- Stage 1: Build -----------------
FROM maven:3.9.3-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar pom.xml y código fuente
COPY pom.xml .
COPY src ./src

# Construir el JAR (skip tests opcionalmente)
RUN mvn clean package -DskipTests

# ----------------- Stage 2: Run -----------------
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copiar el JAR generado desde la etapa anterior
COPY --from=build /app/target/eureka-server-0.0.1-SNAPSHOT.jar app.jar

# Exponer puerto de Eureka
EXPOSE 8761

# Comando para ejecutar Eureka Server
ENTRYPOINT ["java", "-jar", "app.jar"]
