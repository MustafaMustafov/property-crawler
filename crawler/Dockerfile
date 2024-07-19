#FROM openjdk:21-slim
#WORKDIR /app
#COPY target/crawler-0.0.1-SNAPSHOT.jar /app
#EXPOSE 8080
#CMD ["java", "-jar", "crawler-0.0.1-SNAPSHOT.jar"]

FROM maven:3.8.4 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install
FROM openjdk:21-slim
WORKDIR /app
COPY --from=builder /app/target/*.jar ./crawler-0.0.1-SNAPSHOT.jar
EXPOSE 90
CMD ["java", "-jar", "crawler-0.0.1-SNAPSHOT.jar"]

