# Build: os testes rodam no CI (precisam de Docker), então aqui só empacota.
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -q dependency:go-offline
COPY src src
RUN ./mvnw -B -q package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd --system --uid 1001 api
COPY --from=build /app/target/*.jar app.jar
USER api
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080
# Plano gratuito do Render tem 512 MB: heap limitado e GC serial, que gasta menos memória
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=60", "-XX:+UseSerialGC", "-jar", "app.jar"]
