# Migration Notes: Java 8 to Java 11

## Java Version Change
- **Source/target**: Changed from `1.8` to `11` using `<maven.compiler.release>11</maven.compiler.release>`
- The `release` flag replaces separate `source` and `target` properties and provides stronger cross-compilation guarantees.

## Jetty Upgrade (9.2 → 9.4)
- **jetty-maven-plugin**: Upgraded from `9.2.5.v20141112` to `9.4.54.v20240208`
- Jetty 9.4.x is the last Jetty 9 line with full Java 11 support.
- No configuration changes were required; the `jetty-context.xml`, `jetty-env.xml`, and `web-overwrite.xml` descriptors remain compatible.

## Weld/CDI Upgrade (2.2 → 2.4)
- **weld-servlet**: Upgraded from `2.2.7.Final` to `2.4.8.Final`
- Weld 2.4.x is the last CDI 1.2-compatible release and fully supports Java 11.
- CDI injection patterns (`@Inject`, `@ApplicationScoped`, `@Observes`) remain unchanged.

## Plugin Upgrades
| Plugin | Old Version | New Version |
|--------|-------------|-------------|
| maven-compiler-plugin | (default) | 3.11.0 |
| maven-surefire-plugin | (default) | 3.2.5 |
| maven-enforcer-plugin | (none) | 3.5.0 |
| maven-war-plugin | 2.5 | 3.4.0 |

## javax.annotation Handling
- Added explicit `javax.annotation-api:1.3.2` dependency.
- Java 11 removed `javax.annotation` from the JDK (JEP 320). The `@PostConstruct` annotation used in `DefaultGreeting.java` requires this dependency.

## New Test Coverage
- Added JUnit 4.13.2 and Mockito 4.11.0 as test dependencies.
- `DefaultGreetingTest.java`: Unit tests for the CDI bean (interface compliance, greeting format, event handling).
- `MainServletTest.java`: Mock-based servlet tests (content type, HTML output, greeting rendering, CDI event firing).

## CI Workflow Added
- New `.github/workflows/ci.yml` using GitHub Actions.
- Runs on push/PR to master/main.
- Uses `actions/setup-java@v4` with Temurin JDK 11 and Maven caching.
- Executes `mvn -B clean verify` and `mvn -B test`.

## Parent POM Changes
- Added explicit `<modules>` section listing `jetty-maven-cdi`.
- Added `pluginManagement` for `maven-compiler-plugin` and `maven-surefire-plugin`.
- Added `maven-enforcer-plugin` requiring Java `[11,)`.
