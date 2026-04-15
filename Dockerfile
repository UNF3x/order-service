# ===== Stage 1: build =====
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app
# Копируем только то, что влияет на зависимости
COPY pom.xml mvnw mvnw.cmd ./
COPY .mvn .mvn
# Кэшируем зависимости
RUN ./mvnw -B dependency:go-offline
# Копируем остальной код
COPY src ./src
# Сборка
RUN ./mvnw -B clean package -DskipTests

# ===== Stage 2: runtime =====
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# Безопасность: non-root пользователь
RUN useradd -r -u 1001 appuser
# Копируем jar
COPY --from=build /app/target/*jar /app/app.jar
# Права
RUN chown appuser:appuser /app/app.jar
USER appuser
EXPOSE 8080
# JVM оптимизирована под контейнер
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]