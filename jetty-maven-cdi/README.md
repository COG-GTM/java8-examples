README
======

This is an simple setup for testing CDI ([Red Hat JBoss Weld]
(https://docs.jboss.org/weld/reference/latest/en-US/html/)) and Jetty using the 
[jetty-maven-plugin](http://www.eclipse.org/jetty/documentation/current/jetty-maven-plugin.html)

+ Java 17
+ Jetty 9.4.54.v20240208 (Servlet 3.1 container, `javax.*` namespace)
+ Weld 3.1.9.Final (CDI 2.0)

Jetty 9.4 is the last Jetty line that uses the `javax.*` namespace; Jetty 10/11 (and Weld 4+)
require `jakarta.*` and are intentionally not used here.

### Running this example Application

+ Check that Java 17 (or newer) is used
+ Clone this git repository
+ Go to project directory, `java8-examples`
+ Execute `mvn clean install`
+ Go to root directory of this subproject, `jetty-maven-cdi`
+ Start Jetty and go to `http://localhost:8080/` to view the result in your browser

````bash
$ mvn --version
Apache Maven 3.6.3
Maven home: /usr/share/maven
Java version: 17.0.20.1, vendor: Ubuntu, runtime: /usr/lib/jvm/java-17-openjdk-amd64
$ git clone https://github.com/COG-GTM/java8-examples.git
$ cd java8-examples/
$ mvn clean install
[INFO] Scanning for projects...
...
$ cd jetty-maven-cdi/
$ mvn jetty:run
[INFO] Scanning for projects...
[INFO]                                                                         
[INFO] ------------------------------------------------------------------------
[INFO] Building jetty-maven-cdi 1.0.0-SNAPSHOT
[INFO] ------------------------------------------------------------------------
...
[INFO] jetty-9.4.54.v20240208; ... jvm 17.0.20.1+1-1-22.04-Ubuntu
[INFO] CdiSpiDecorator enabled in ServletContext@o.e.j.m.p.JettyWebAppContext...
INFO: WELD-000900: 3.1.9 (Final)
INFO: WELD-ENV-001213: Jetty CDI SPI support detected, CDI injection will be available in Listeners, Servlets and Filters.
[INFO] Started ServerConnector@d66502{HTTP/1.1, (http/1.1)}{0.0.0.0:8080}
[INFO] Started Jetty Server
````

Opening `http://localhost:8080/` shows `Hello web-overwrite.xml!` (JNDI `env-entry` from
`web-overwrite.xml`) and the console logs `BeanManager injection succeeded`,
`BeanManager JNDI lookup succeeded` and `Observed event: Simple test event`.

### Jetty configuration

To enable CDI, you need to configure Jetty first. Several online resources describe the 
configuration in a different way. Most importantly the official Jetty and Weld documentation
are not consistent.

With Jetty 9.4 this example uses the `jetty-cdi` integration in `CdiSpiDecorator` mode:

+ `org.eclipse.jetty:jetty-cdi` is a dependency of `jetty-maven-plugin`. Its
  `CdiServletContainerInitializer` makes Jetty decorate Servlets, Filters and Listeners via the
  CDI SPI.
+ `WEB-INF/jetty-context.xml` exposes that initializer to the webapp (the equivalent of the
  Jetty distribution's `cdi` module).
+ Weld (`weld-servlet-core`) is deployed inside the webapp (`WEB-INF/lib`). Jetty 9.4's
  `jetty-maven-plugin` hides `javax.enterprise.*` classes on the plugin classpath from webapps,
  so Weld can no longer be added as a plugin dependency as was done with Jetty 9.2.
+ `web.xml` disables Weld bean archive isolation (`org.jboss.weld.environment.servlet.archive.isolation=false`)
  so the JNDI `BeanManager` defined in `jetty-env.xml` resolves when running `mvn jetty:run`.

+ [Jetty documentation](http://www.eclipse.org/jetty/documentation/current/framework-weld.html)
+ [Weld documentation](https://docs.jboss.org/weld/reference/latest/en-US/html/environments.html)

### How to setup a CDI enabled application?

+ Add `javax.enterprise:cdi-api:2.0.SP1` and `javax.annotation:javax.annotation-api:1.3.2`
(removed from the JDK in Java 11), scope `provided` to your (maven) dependencies
+ Add `org.jboss.weld.servlet:weld-servlet-core:3.1.9.Final`, scope `runtime`, to your
dependencies
+ Add `org.eclipse.jetty:jetty-cdi:9.4.54.v20240208` as a dependency for `jetty-maven-plugin`
+ Managed beans must have a default constructor and may not be `final` (must be proxiable)
+ Managed beans declaring a passivating scope must be passivation capable, 
implement `java.io.Serializable` and all `@Interceptors` must be Serializable as well

### Notes

+ CDI injection is available in 
    + Servlets and Filters (Jetty 7.2+)
    + Listeners (Jetty 9.1.1+)
+ [Jetty 9.1.0+ requires Weld 2.2.0+](https://issues.jboss.org/browse/WELD-1561)
+ Jetty 9.4.20+ requires Weld 3.1.2+ for the `jetty-cdi` (CdiSpiDecorator) integration
+ Jetty 9.4 implements Servlet 3.1; the project compiles against `javax.servlet-api:4.0.1`
(`provided`) but must not use Servlet 4.0-only APIs when running on Jetty 9.4
+ Transactional events not available in a non-Java EE environment 

### References

+ [JSR 299: Contexts and Dependency Injection for the Java EE platform]
(https://jcp.org/en/jsr/detail?id=299). CDI 1.0, Part of Java EE 6
+ [JSR 346: Contexts and Dependency Injection for Java EE 1.1]
(https://jcp.org/en/jsr/detail?id=346). CDI 1.1, Part of Java EE 7 release and [CDI 1.2]
(http://www.cdi-spec.org/news/2014/04/14/CDI-1_2-released/) maintenance release 
+ [The Java EE Tutorial, Contexts and Dependency Injection]
(https://docs.oracle.com/javaee/7/tutorial/partcdi.htm#GJBNR)
+ [Weld - CDI: Contexts and Dependency Injection for the Java EE platform]
(https://docs.jboss.org/weld/reference/latest/en-US/html/index.html)
+ [Must read about CDI 2.0](http://www.next-presso.com/2014/03/forward-cdi-2-0/)
+ [Introduction to JNDI](http://archive.oreilly.com/pub/a/onjava/excerpt/java_servlets_ch12/index.html?page=3)
+ [Working with Jetty JNDI](http://www.eclipse.org/jetty/documentation/current/using-jetty-jndi.html)
