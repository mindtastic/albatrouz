# OpenAPI Tira Code generation

## Overview

This is small extension to `openapi-codegen` for building openapi specifications with tira extensions.

## What's OpenAPI

The goal of OpenAPI is to define a standard, language-agnostic interface to REST APIs which allows both humans and computers to discover and understand the capabilities of the service without access to source code, documentation, or through network traffic inspection.
When properly described with OpenAPI, a consumer can understand and interact with the remote service with a minimal amount of implementation logic.
Similar to what interfaces have done for lower-level programming, OpenAPI removes the guesswork in calling the service.

Check out [OpenAPI-Spec](https://github.com/OAI/OpenAPI-Specification) for additional information about the OpenAPI project, including additional libraries with support for other languages and more.

## How do I use this?

### Modify codegenerator

You _will_ need to make changes in at least the following:

`TiraGenerator.java`

Templates in this folder:

`src/main/resources/tira`

### Building

It's a maven project, so you can just run:

```bash
mvn package
```

If you want to run it using docker (assuming you are in this directory):

```bash
docker run --rm -v $(pwd)/generator:/src -v $(pwd):/out maven mvn package -f /src -DoutputDirectory=/out
```

In your generator project. A single jar file will be produced in `target`. You can now use that with [OpenAPI Generator](https://openapi-generator.tech):

## How to use the built generator

For mac/linux:

```bash
java -cp /path/to/openapi-generator-cli.jar:/path/to/your.jar org.openapitools.codegen.OpenAPIGenerator generate -g tira -i /path/to/openapi.yaml -o ./test
```

(Do not forget to replace the values `/path/to/openapi-generator-cli.jar`, `/path/to/your.jar` and `/path/to/openapi.yaml` in the previous command)

For Windows users, you will need to use `;` instead of `:` in the classpath, e.g.

```bash
java -cp /path/to/openapi-generator-cli.jar;/path/to/your.jar org.openapitools.codegen.OpenAPIGenerator generate -g tira -i /path/to/openapi.yaml -o ./test
```

Now your templates are available to the client generator and you can write output values.
