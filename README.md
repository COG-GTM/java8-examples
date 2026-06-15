README
======

This repository contains simple example projects of various Java based projects.

The codebase was migrated from Java 8 to **Java 11 (LTS)**. See
[`MIGRATION_NOTES.md`](MIGRATION_NOTES.md) for details.

Requirements
------------

+ JDK 11 (e.g. Eclipse Temurin / Adoptium 11). The build enforces `>= 11`.
+ Apache Maven 3.6+

Build
-----

From the repository root (builds all modules):

```bash
mvn clean verify
```

Continuous integration builds on Temurin JDK 11 via GitHub Actions
(`.github/workflows/build.yml`).
