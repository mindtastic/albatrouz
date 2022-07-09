# Build project on a maven container
FROM maven:3.8.6-openjdk-18 AS builder

ARG GITHUB_WORKSPACE=/albatrouz
ARG BUILD_DIR=${GITHUB_WORKSPACE}

# Create directory if not exists on github
RUN mkdir -p ${BUILD_DIR}

COPY ./pom.xml ${BUILD_DIR}/pom.xml
COPY ./src ${BUILD_DIR}/src

# Increase the stack size, otherwise the assembly stage runs into a stackoverflow exception
ENV MAVEN_OPTS "-Xss2m ${MAVEN_OPTS}"
RUN cd ${BUILD_DIR} && \
    mvn compile assembly:single 

# Run environment
FROM openjdk:18-jdk

ARG GITHUB_WORKSPACE=/albatrouz
ENV ALBATROUZ_DIR=${GITHUB_WORKSPACE}

COPY --from=builder ["${ALBATROUZ_DIR}/target/tira-openapi-generator-1.0.0-jar-with-dependencies.jar", "${ALBATROUZ_DIR}/openapi-generator.jar"]

# Setup entrypoint.sh and make it executable
COPY ./entrypoint.sh ./entrypoint.sh
RUN chmod +x ./entrypoint.sh

ENTRYPOINT [ "entrypoint.sh" ]
