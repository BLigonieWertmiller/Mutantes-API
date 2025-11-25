# ========================================
# ETAPA 1: BUILD
# ========================================
FROM eclipse-temurin:21-jdk-alpine as build

WORKDIR /app

# OJO AQUÍ: Ya NO ponemos "Mutantes/" antes de los archivos.
# Como el contexto es la carpeta interna, los archivos están ahí mismo.

COPY gradle gradle
COPY build.gradle settings.gradle gradlew ./

RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon

COPY src src

RUN ./gradlew bootJar --no-daemon

# ========================================
# ETAPA 2: RUNTIME
# ========================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
EXPOSE 8080

COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
