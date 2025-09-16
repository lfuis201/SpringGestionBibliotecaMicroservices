# ----------------- Stage 1: Build -----------------
FROM maven:3.9.3-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar archivos pom.xml y código fuente
COPY pom.xml .
COPY src ./src

# Construir el JAR sin tests
RUN mvn clean package -DskipTests

# ----------------- Stage 2: Run -----------------
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copiar el JAR generado
COPY --from=build /app/target/api-gateway-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto del gateway
EXPOSE 8080

# Comando para ejecutar el microservicio
ENTRYPOINT ["java", "-jar", "app.jar"]
