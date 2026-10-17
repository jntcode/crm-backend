FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*
RUN mvn -B clean verify -q

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# Production profile is the safe default: the container fails fast when the
# required database/JWT configuration is missing instead of silently using H2.
# Run locally with -e SPRING_PROFILES_ACTIVE=dev -e JWT_SECRET=... instead.
ENV SPRING_PROFILES_ACTIVE=prod
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
