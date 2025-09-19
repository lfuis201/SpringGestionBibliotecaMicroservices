# ============================
# Stage 1: Build (Maven)
# ============================
FROM maven:3.9.3-eclipse-temurin-17 AS build
WORKDIR /app

# Copiamos pom.xml y descargamos dependencias (cache eficiente)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiamos el código fuente
COPY src ./src

# Compilamos y empaquetamos, limpiando caché de Maven
RUN mvn clean package -DskipTests --no-transfer-progress \
    && rm -rf ~/.m2/repository

# ============================
# Stage 2: Run (Ligero con JRE)
# ============================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiamos el JAR desde la etapa de build
COPY --from=build /app/target/auth-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8084

# Ejecutamos la aplicación en el puerto 80804
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=8084"]
