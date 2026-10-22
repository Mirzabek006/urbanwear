# 1-bosqich: Loyihani yig'ish (Build)
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# 2-bosqich: Ishga tushirish (Run)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

ENV PORT=8080
EXPOSE 8080

# Xotirani tejash (512MB bepul serverlar uchun)
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-Xmx384m", "-jar", "app.jar"]
