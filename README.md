# Albatrouz

Praise the albatrouz. It got [TILT](https://github.com/Transparency-Information-Language/meta)-Wings

## What is this for?

The albatross will tell you for himself. Volume up and tilt around the house! 🦅

https://user-images.githubusercontent.com/3830015/176738970-825fa145-c848-4893-bfb5-fdd5c6161967.mp4

## Build

The project provides three ways of building for different use cases:

1. Build as an openapi-generator plugin.
2. Build a complete openapi-generator bundle with albatrouz baked in.
3. Build a docker container that has an openapi-generator as entrypoint.

For the first two, you need a local java development environment with:

- OpenJDK 18
- Maven

For the docker build, you need Docker Desktop. Building in other container environments works as well, but are neither instructed nor officially supported.

### Build as a plugin

```bash
mvn package
```

### Build a jar buundle

```bash
mvn compile assembly:assembly
```

### Build docker container

```bash
docker build -t albatrouz
```

## Run

In general, albatrouz is started using `openapi-generator-cli` as follows:

```bash
openapi-generator generate -g tira 
```

The actual command line call depends on the way you built the project. 

### As a plugin

To run albatrouz as a plugin, you need to provide an `openapi-generator` installation. Then, add the plugin to the classpath. This differs on *Windows* slighty from *unixoid* systems. If you are using *WSL2* or a *cygwin/mingw* based shell and encouter issues, try the windows syntax.

Execute on UNIXish systems:

```bash
PARAMS="-g tira -i /my/openapi.yaml -o outputDir/"
OPENAPI_GENERATOR_JAR="path/to/your/openapi-generator.jar"

java -cp target/tira-openapi-generator-1.0.0.jar:$OPENAPI_GENERATOR_JAR org.openapitools.codegen.OpenAPIGenerator generate $PARAMS
```

For Windows, you will need to use `;` instead of `:` in the classpath:

```cmd
SET PARAMS=-g tira -i .\my\openapi.yaml -o .\outputDir
SET OPENAPI_GENERATOR_JAR=C:\thePath\to\openapi-generator.jar

java -cp "target/tira-openapi-generator-1.0.0.jar;%OPENAPI_GENERATOR_JAR%" org.openapitools.codegen.OpenAPIGenerator generate %PARAMS%
```

### As a standalone

As a standalone build, you just need to execute the generated `.jar`:

```bash
PARAMS="-g tira -i .\my\openapi.yaml -o .\outputDir"

java -jar target/tira-openapi-generator-1.0.0-jar-with-dependencies.jar generate $PARAMS
```

### Docker

As the `.jar` is set as entrypoint of the built image, running  `docker run` and prepending the command line arguments is sufficient

```bash
docker run --rm -v $(pwd):/openapi albatrouz generate -g tira -i /openapi/openapi.yaml -o /openapi/outDir
```
