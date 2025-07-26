FROM maven:3.9.11-ibm-semeru-21-noble AS build

WORKDIR /app

COPY pom.xml .
COPY src/ ./src/

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]