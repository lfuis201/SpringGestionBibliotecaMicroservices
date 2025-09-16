# ----------------- Stage 1: Build -----------------
FROM maven:3.9.3-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar los archivos pom.xml y código fuente
COPY pom.xml .
COPY src ./src

# Construir el JAR sin ejecutar tests
RUN mvn clean package -DskipTests

# ----------------- Stage 2: Run -----------------
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copiar el JAR generado desde la etapa de build
COPY --from=build /app/target/reservation-service-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto del microservicio (Reservation Service usa 8083)
EXPOSE 8083

# Comando para ejecutar el microservicio
ENTRYPOINT ["java", "-jar", "app.jar"]
