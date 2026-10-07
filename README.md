README
======

This repository contains simple example projects of various Java based projects.
Originally written for Java 8, the examples now target **Java 17** (`maven.compiler.release=17`)
and still use the `javax.*` namespace (no Jakarta EE migration).

All examples are built and tested using the following software stack:

+ OpenJDK 17 (the build enforces JDK 17+ via `maven-enforcer-plugin`)
+ Apache Maven 3.6.3+

Build all modules from the repository root with `mvn clean install`.

