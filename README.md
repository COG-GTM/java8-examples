README
======

This repository contains small, self-contained example projects built on a
modern Java / Jakarta EE stack. It was originally a "Java 8 examples" repo
(circa 2014) and has been **modernized to Java 21 / Jakarta EE 10**.

All examples are developed and tested using the following software stack:

+ Java 21 (Eclipse Temurin, or any OpenJDK 21 distribution)
+ Apache Maven 3.9.x — provided via the included [Maven Wrapper](https://maven.apache.org/wrapper/) (`./mvnw`), so no system Maven install is required
+ Any modern IDE (IntelliJ IDEA, Eclipse, VS Code, NetBeans, ...)
+ Tested on current Ubuntu / macOS / Windows

### Building

```bash
./mvnw clean verify
```

### Modules

+ [`jetty-maven-cdi`](jetty-maven-cdi/README.md) — CDI (Jakarta Contexts and
  Dependency Injection) running on an embedded Jetty 12 servlet container via
  the Jetty Maven plugin, using JBoss Weld as the CDI implementation.
