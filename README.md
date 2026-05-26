README
======

This repository contains simple example projects of various Java 11 based projects.

All examples are developed and tested using the following software stack:

+ Ubuntu 22.04 LTS (or later)
+ OpenJDK 11 (Temurin recommended)
+ Apache Maven 3.6+

## Building

```bash
mvn clean verify
```

## Running Tests

```bash
mvn test
```

## Running Jetty (CDI example)

```bash
cd jetty-maven-cdi
mvn jetty:run
```

Then open http://localhost:8080/ in your browser.

## Modules

- **jetty-maven-cdi**: Demonstrates CDI 1.2 (Weld) integration with embedded Jetty 9.4 servlet container.

## Migration

This project was migrated from Java 8 to Java 11. See [MIGRATION_NOTES.md](MIGRATION_NOTES.md) for details.
