## Stage 1: Build the application
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /App

COPY pom.xml ./

# Download dependencies offline to reduce build time
RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM amazoncorretto:21

WORKDIR /App

COPY --from=build /App/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
