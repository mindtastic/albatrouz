# Build project on a maven container
FROM maven:3.8.6-openjdk-18 AS builder

WORKDIR /build

COPY ./pom.xml ./pom.xml
COPY ./src ./src

# Increase the stack size, otherwise the assembly stage runs into a stackoverflow exception
ENV MAVEN_OPTS "-Xss2m ${MAVEN_OPTS}"
RUN mvn compile assembly:assembly 

# Run environment
FROM openjdk:18-jdk

WORKDIR /app

COPY --from=builder ["/build/target/tira-openapi-generator-1.0.0-jar-with-dependencies.jar", "./openapi-generator.jar"]

ENTRYPOINT [ "java", "-jar", "openapi-generator.jar" ]