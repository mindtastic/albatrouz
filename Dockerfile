# Build project on a maven container
FROM maven:3.8.6-openjdk-18 AS builder

WORKDIR /build

COPY ./pom.xml ./pom.xml
COPY ./src ./src

RUN mvn compile assembly:assembly 

# Run environment
FROM openjdk:18-jdk

WORKDIR /app

COPY --from=builder ["/build/target/tira-openapi-generator-1.0.0-jar-with-dependencies.jar", "./openapi-generator.jar"]

ENTRYPOINT [ "java", "-jar", "openapi-generator.jar" ]