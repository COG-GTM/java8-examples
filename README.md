README
======

This repository contains simple example projects demonstrating CDI (Weld) integration
with Jetty, originally developed for Java 8 and migrated to **Java 11 (LTS)**.

## Requirements

+ **JDK 11** or later (e.g., Eclipse Temurin 11)
+ Apache Maven 3.6+

## Building

```bash
mvn clean install
```

This builds the parent reactor and the `jetty-maven-cdi` module, producing a WAR
artifact.

## Running the Demo

```bash
cd jetty-maven-cdi
mvn jetty:run
```

Then open <http://localhost:8080/> to see the CDI-injected greeting.

## Migration from Java 8

This project was migrated from Java 8 to Java 11. See
[MIGRATION_NOTES.md](MIGRATION_NOTES.md) for details on what changed
(build config, dependency replacements, Jetty/Weld upgrades, and known
warnings).

## Original Software Stack (historic reference)

+ Ubuntu 14.04.1 LTS
+ Oracle Java (JDK) 1.8.0_25
+ NetBeans IDE 8.0.2
+ Apache Maven 3.2.2
