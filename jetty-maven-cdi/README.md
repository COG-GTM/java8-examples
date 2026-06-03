README
======

A small setup demonstrating **CDI** ([Red Hat JBoss
Weld](https://weld.cdi-spec.org/)) on an embedded **Jetty 12** servlet container
using the
[jetty-ee10-maven-plugin](https://eclipse.dev/jetty/documentation/jetty-12/programming-guide/index.html#jetty-maven-plugin).

+ Java 21 / Jakarta EE 10
+ Jetty 12.0.x (`jetty-ee10-maven-plugin`)
+ Weld 5.1.x (CDI 4.0) via `weld-servlet-shaded`
+ JUnit 5 + `weld-junit5` for tests

> **Note on the `javax` → `jakarta` migration.** This example was upgraded from
> Jetty 9 / Weld 2 / CDI 1.2. All `javax.*` EE namespaces were migrated to
> `jakarta.*`, and the deployment descriptors now use the Jakarta EE 10 schemas.

### Running this example application

+ Make sure Java 21 is active (`java -version`)
+ From the repository root, build everything: `./mvnw clean install`
+ Change into this module: `cd jetty-maven-cdi`
+ Start Jetty: `../mvnw jetty:run`
+ Open `http://localhost:8080/` in your browser

```bash
$ ../mvnw jetty:run
...
[INFO] CdiSpiDecorator enabled in ServletContext@...
INFO: WELD-ENV-001213: Jetty CDI SPI support detected, CDI injection will be available in Listeners, Servlets and Filters.
[INFO] Started ServerConnector@...{HTTP/1.1, (http/1.1)}{0.0.0.0:8080}
[INFO] Started oejs.Server@...
```

The page is rendered by `HelloServlet`, which has a `GreetingService` injected
via CDI. Each request also fires a CDI event observed by `AppEventObserver`, and
passes through `LoggingFilter` (which is itself CDI-injected).

### What's in here

| Component | Demonstrates |
|-----------|--------------|
| `HelloServlet` | `@WebServlet` with `@Inject` of a CDI bean |
| `GreetingService` | `@ApplicationScoped` bean with `@PostConstruct` |
| `AppEventProducer` / `AppEventObserver` | `Event<T>` firing and `@Observes` |
| `LoggingFilter` | `@WebFilter` with `@Inject` |

### Jetty 12 + Weld configuration

Enabling CDI on Jetty 12 is considerably simpler than on Jetty 9:

1. Add the Jakarta APIs (`jakarta.servlet-api`, `jakarta.enterprise.cdi-api`,
   `jakarta.annotation-api`) as `provided` dependencies, and
   `weld-servlet-shaded` as a `runtime` dependency.
2. Add `org.eclipse.jetty.ee10:jetty-ee10-cdi` as a dependency of the
   `jetty-ee10-maven-plugin` so Jetty's CDI integration is on the container
   classpath.
3. Enable the integration via `WEB-INF/jetty-context.xml`, which sets the
   `org.eclipse.jetty.cdi` context attribute to `CdiSpiDecorator`. In this mode
   Jetty uses the Weld `BeanManager` (the CDI SPI) to decorate and inject
   Servlets, Filters and Listeners.
4. Provide a CDI 4.0 `WEB-INF/beans.xml` with
   `bean-discovery-mode="annotated"`.

The Jetty 9 era files are **no longer needed**:

+ `jetty-env.xml` (manual JNDI `BeanManager` binding via
  `org.jboss.weld.resources.ManagerObjectFactory`) — removed. With Jetty 12 +
  Weld 5 the `BeanManager` is wired automatically by the `CdiSpiDecorator`, so
  no JNDI binding is required.
+ The `serverClasses` Decorator hack in `jetty-context.xml` — replaced by the
  `org.eclipse.jetty.cdi` attribute described above.

`web-overwrite.xml` is still applied by the plugin's `overrideDescriptor` and
overrides the `helloName` JNDI `env-entry` from `web.xml`; that is why the
running servlet greets `Hello web-overwrite.xml!`.

### Building a Docker image

A multi-stage [`Dockerfile`](Dockerfile) is provided. Build it from the
repository root (the build needs the parent POM and Maven Wrapper):

```bash
docker build -f jetty-maven-cdi/Dockerfile -t jetty-maven-cdi .
docker run --rm -p 8080:8080 jetty-maven-cdi
```

### Notes

+ CDI injection is available in Servlets, Filters and Listeners.
+ Transactional events are not available in this non-Jakarta-EE-server
  environment (Weld logs `WELD-000101`).

### References

+ [Jakarta Contexts and Dependency Injection 4.0](https://jakarta.ee/specifications/cdi/4.0/)
+ [Eclipse Jetty 12 — Programming Guide](https://eclipse.dev/jetty/documentation/jetty-12/programming-guide/index.html)
+ [Weld — CDI Reference Implementation](https://weld.cdi-spec.org/)
