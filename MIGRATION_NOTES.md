# Java 8 → 11 Migration Notes

This document summarizes the migration of this repository from Java 8 to
**Java 11 (LTS)**. The goal was a minimal, behavior-preserving upgrade of the
build toolchain and source compatibility — no functional rewrites.

## Summary of changes

| Area | Before (Java 8) | After (Java 11) |
| --- | --- | --- |
| Compiler level | `maven.compiler.source/target = 1.8` | `maven.compiler.release = 11` |
| `maven-compiler-plugin` | 3.1 (default) | 3.11.0, `-Xlint:all` |
| `maven-war-plugin` | 2.5 | 3.4.0 |
| `jetty-maven-plugin` | 9.2.5.v20141112 | 9.4.53.v20231009 |
| `weld-servlet` | 2.2.7.Final (plugin classpath) | 2.4.8.Final, `runtime` scope (packaged in WAR) |
| Enforcer | none | `maven-enforcer-plugin` 3.5.0, `requireJavaVersion [11,)` |
| Surefire / Failsafe / Javadoc | none / default | 3.2.5 / 3.2.5 / 3.6.3 (managed) |
| CI | none | GitHub Actions, Temurin JDK 11, Maven cache |
| Reactor | root POM had no `<modules>` | root POM aggregates `jetty-maven-cdi` |

## Removed JDK modules / API replacements

Java 11 removed the Java EE / CORBA modules that shipped with the JDK through
Java 8 (JEP 320). The only affected API in this codebase was JSR-250 common
annotations:

- **`javax.annotation.PostConstruct`** (used in `DefaultGreeting`) lived in the
  `java.xml.ws.annotation` module, which was removed in Java 11. Added an
  explicit dependency to restore it without any code change:

  ```xml
  <dependency>
      <groupId>javax.annotation</groupId>
      <artifactId>javax.annotation-api</artifactId>
      <version>1.3.2</version>
      <scope>provided</scope>
  </dependency>
  ```

  Scope is `provided` because the annotation API is supplied at runtime by the
  Weld/Jetty container; it is only needed to compile.

No JAXB, JAX-WS, CORBA, JavaFX, or Nashorn usage was found, so no further
replacements were required. The CDI / Servlet stack stays on the `javax.*`
namespace (CDI 1.2) — moving to `jakarta.*` is a larger, separate effort and was
intentionally left out of scope.

## Encapsulation / reflection (JPMS)

The project remains on the **classpath** (no `module-info.java`) for minimal
change. The build does not rely on any `--add-opens` / `--add-exports` flags.
Weld performs reflective access at runtime; `weld-servlet` was bumped to
`2.4.8.Final` (the last CDI 1.2 line) for better Java 9+ compatibility, and the
`jetty-maven-plugin` to `9.4.x`, which runs on Java 11.

## Jetty 9.4 runtime configuration (`mvn jetty:run`)

Jetty 9.2 ran on Java 8 only, so the plugin was bumped to the last Jetty 9.x
line (still `javax.servlet`, runs on Java 11). The bump required a few config
adjustments to keep `mvn jetty:run` working with the same behavior as before
(the servlet renders `Hello web-overwrite.xml!` and CDI `BeanManager` injection
succeeds):

- **Config DTD**: `jetty-context.xml` / `jetty-env.xml` referenced the obsolete
  `configure_9_0.dtd` (no longer bundled by Jetty 9.4, so it was fetched over
  the network and failed to parse). Updated to the `configure_9_3.dtd`
  declaration, which Jetty 9.4 resolves locally. The `<Ref id="wac"/>` was also renamed to
  `<Ref refid="wac"/>` per the newer DTD.
- **JNDI provider**: the 9.2 plugin wired Jetty's JNDI implicitly. For 9.4 the
  two factory system properties are set on the plugin and
  `org.eclipse.jetty.jndi.` is un-hidden from the webapp classloader (via the
  `serverClasses` `-` prefix) so the `InitialContextFactory` is loadable when
  the env context is created.
- **Weld placement**: `weld-servlet` moved from the plugin classpath into the
  WAR (`runtime` scope) so the webapp classloader sees the CDI implementation
  directly under Jetty 9.4's stricter classloader isolation.

These changes affect `mvn jetty:run` only, not the CI build.

## Security / TLS

No application TLS configuration, keystores, or certificates are present in this
repo, so there was nothing to revalidate. On Java 11, TLS 1.3 is enabled by
default and the default keystore type is PKCS12 — relevant only if this example
is later wired to live endpoints.

## GC / logging

No GC tuning or legacy JVM flags are committed in this repo, so no Unified
Logging migration was needed. Java 11 defaults to the G1 collector.

## Known warnings (non-blocking)

`-Xlint:all` surfaces two pre-existing, behavior-neutral warnings; `-Werror`
was intentionally **not** enabled to avoid failing the build on these without a
functional rewrite:

- `MainServlet` has no `serialVersionUID` (it extends `HttpServlet`).
- `DefaultGreeting` uses a raw `javax.naming.NamingEnumeration`.

## Validation

- Java 8 baseline: `mvn clean install` built the WAR successfully before changes.
- Java 11: `mvn clean verify` builds the WAR successfully; the enforcer rule
  confirms the JDK is `>= 11`.
- Runtime parity: `mvn jetty:run` on JDK 11 serves `http://localhost:8080/`
  returning `Hello web-overwrite.xml!` with `BeanManager injection succeeded`,
  matching the Java 8 / Jetty 9.2 baseline byte-for-byte (including the same
  pre-existing partial `java:comp/BeanManager` JNDI lookup behavior).
- There are no unit/integration tests in the project (coverage 0% on both 8 and
  11), so there is no coverage delta to report.

## Follow-ups (out of scope)

- Migrate `javax.*` → `jakarta.*` (CDI / Servlet) for Jakarta EE 9+.
- Add unit tests and code coverage.
- Consider `-Werror` once the lint warnings above are addressed.
