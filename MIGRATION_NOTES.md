# Java 8 → 11 Migration Notes

## Summary

This project has been migrated from Java 8 to **Java 11 (LTS)**. The migration
updates build tooling, replaces removed JDK modules with external dependencies,
upgrades the embedded Jetty server and Weld CDI runtime for Java 11 compatibility,
and adds CI on JDK 11.

## Build Changes

| Area | Before | After |
|------|--------|-------|
| Compiler target | `source/target 1.8` | `<release>11</release>` |
| maven-compiler-plugin | 3.1 (default) | 3.11.0 (`-Xlint:all`) |
| maven-surefire-plugin | 2.x (default) | 3.2.5 |
| maven-failsafe-plugin | (none) | 3.2.5 (managed) |
| maven-war-plugin | 2.5 | 3.4.0 |
| maven-javadoc-plugin | (none) | 3.6.3 (`-Xdoclint:none`) |
| maven-enforcer-plugin | (none) | 3.5.0 (`requireJavaVersion [11,)`) |
| Parent reactor | no modules listed | `jetty-maven-cdi` included |

## Removed JDK Module Replacements

| Removed Module | Usage | Replacement |
|---------------|-------|-------------|
| `java.xml.ws.annotation` (JEP 320) | `javax.annotation.PostConstruct` in `DefaultGreeting` | `javax.annotation:javax.annotation-api:1.3.2` (provided) |

No JAXB, JAX-WS, JavaFX, or CORBA usage was found in this codebase.

## Runtime / Dependency Upgrades

| Component | Before | After | Reason |
|-----------|--------|-------|--------|
| jetty-maven-plugin | 9.2.5.v20141112 | 9.4.57.v20241219 | Java 11 support; last javax.servlet line |
| weld-servlet | 2.2.7.Final (plugin dep) | 2.4.8.Final (module runtime dep) | Java 11 support; CDI 1.2 preserved |

Weld was moved from a jetty-plugin `<dependency>` to a module-level `runtime`
dependency. Jetty 9.4's stricter webapp classloader isolation no longer exposes
plugin dependencies to the webapp; placing Weld in WEB-INF/lib is the modern
supported model for CDI on embedded Jetty.

## Jetty Configuration Updates

- `jetty-context.xml` / `jetty-env.xml`: DOCTYPE updated to valid PUBLIC id
  (`-//Jetty//Configure//EN"`) so the DTD resolves locally instead of requiring
  a network fetch.
- `jetty-env.xml`: `<Ref id="wac"/>` → `<Ref refid="wac"/>` (modern Jetty XML
  syntax; avoids duplicate-ID validation error).
- `jetty-context.xml`: exposed `org.eclipse.jetty.jndi.*` via serverClasses for
  JNDI factory resolution.
- Plugin config: `java.naming.factory.initial` set as system property to
  bootstrap Jetty's JNDI context factory.

## Encapsulation / Illegal Reflective Access

**Known warning (non-fatal):**
```
WARNING: Illegal reflective access by org.jboss.classfilewriter.ClassFile$1
  → java.lang.ClassLoader.defineClass(...)
```

This is a Weld 2.x limitation: its proxy generator uses `ClassLoader.defineClass`
via reflection. On Java 11 (default `--illegal-access=permit`), this only
produces a warning; the application runs correctly.

**Removal plan:** Upgrade to Weld 3.x+ / CDI 2.0 in a follow-up migration. Weld
3.x uses legal bytecode generation that does not require reflective access to
internal JDK methods.

No `--add-opens` flags are required on Java 11.

## GC & Logging

The default GC on JDK 11 is G1 (unchanged from JDK 9+). No explicit GC flags
were used in this project. If GC logging is needed in the future, use Unified
Logging:
```
-Xlog:gc*:file=gc.log:time,uptime,level,tags
```

## Security / TLS

No TLS endpoints or keystores are used in this demo project. JDK 11 defaults to
TLS 1.3; no action required.

## CI

A GitHub Actions workflow (`.github/workflows/java11-build.yml`) builds and
packages the project on JDK 11 (Temurin) with Maven caching.

## Follow-ups

1. Upgrade to Weld 3.x/CDI 2.0 to eliminate the reflective-access warning.
2. Consider Jakarta EE namespace migration (`javax.*` → `jakarta.*`) when
   targeting Jetty 10+/11+ in a future Java 17+ migration.
3. Optionally add unit/integration tests.
