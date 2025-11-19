# ---------- STAGE 1: build ----------
FROM eclipse-temurin:17-jdk-jammy AS build

WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN ./mvnw -q -e -DskipTests package

# ---------- STAGE 2: runtime ----------
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=build /build/target/healthsystem-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
