README
======

This is an simple setup for testing CDI ([Red Hat JBoss Weld]
(https://docs.jboss.org/weld/reference/latest/en-US/html/)) and Jetty using the 
[jetty-maven-plugin](http://www.eclipse.org/jetty/documentation/current/jetty-maven-plugin.html)

+ Java 17
+ Jetty 10.0.25
+ Weld 3.1.9.Final (CDI 2.0)

The `javax.*` namespace is retained (no `jakarta.*` migration), which makes Jetty 10
the terminal choice: Jetty 11+ is `jakarta`-only. Note that Jetty 10 is past end-of-life.

### Running this example Application

+ Check that Java 17 is used
+ Clone this git repository
+ Go to project directory, `java8-examples`
+ Execute `mvn clean install`
+ Go to root directory of this subproject, `jetty-maven-cdi`
+ Run `mvn jetty:run-war` and go to `http://localhost:8080/` to view the result in your browser

**Use `jetty:run-war`, not `jetty:run`.** The example demonstrates the `BeanManager`
JNDI binding from `jetty-env.xml`, which Weld's `ManagerObjectFactory` only resolves for a
bean archive whose id contains `WEB-INF/classes`. `jetty:run` serves classes from
`target/classes`, so the JNDI lookup fails (`WELD-001300`); the assembled WAR run by
`jetty:run-war` resolves it. CDI injection into the servlet works under both.

````bash
$ mvn --version
Apache Maven 3.9.6
Java version: 17.0.13, vendor: Eclipse Adoptium
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", arch: "amd64", family: "unix"
$ git clone https://github.com/rmuller/java8-examples.git
Cloning into 'java8-examples'...
remote: Counting objects: 43, done.
remote: Compressing objects: 100% (26/26), done.
remote: Total 43 (delta 4), reused 38 (delta 2)
Unpacking objects: 100% (43/43), done.
Checking connectivity... done.
$ cd java8-examples/
$ mvn clean install
[INFO] Scanning for projects...
...
$ cd jetty-maven-cdi/
$ mvn jetty:run-war
[INFO] Scanning for projects...
[INFO] Building jetty-maven-cdi 1.0.0-SNAPSHOT
...
INFO: WELD-000900: 3.1.9 (Final)
INFO: WELD-ENV-001212: Jetty CdiDecoratingListener support detected, CDI injection will be available in Listeners, Servlets and Filters.
DefaultGreeting#init()
BeanManager injection succeeded
BeanManager JNDI lookup succeeded
INFO:oejs.Server:main: Started Server@...{STARTING}[10.0.25]
INFO:oejs.AbstractConnector:main: Started ServerConnector@...{HTTP/1.1}{0.0.0.0:8080}
[INFO] Started Jetty Server
````

### Jetty configuration

To enable CDI, you need to configure Jetty first. Several online resources describe the 
configuration in a different way. Most importantly the official Jetty and Weld documentation
are not consistent.

+ [Jetty documentation](http://www.eclipse.org/jetty/documentation/current/framework-weld.html)
+ [Weld documentation](https://docs.jboss.org/weld/reference/latest/en-US/html/environments.html)

### How to setup a CDI enabled application?

+ Add `javax.enterprise:cdi-api:2.0.SP1` and `javax.annotation:javax.annotation-api:1.3.2`,
scope `provided`, to your (maven) dependencies (the latter is required to compile
`@PostConstruct` on Java 11+, since it was removed from the JDK)
+ Add `org.jboss.weld.servlet:weld-servlet-shaded:3.1.9.Final`, scope `runtime`, as a
*webapp* dependency so Weld is bundled in `WEB-INF/lib` (Jetty provides no CDI)
+ Add `org.eclipse.jetty:jetty-cdi:10.0.25` as a dependency of `jetty-maven-plugin`, and set
the context init-param `org.eclipse.jetty.cdi=CdiDecoratingListener` (see `jetty-context.xml`).
Jetty 10 removed the `ServletContextHandler.Decorator` that older Weld auto-detection relied
on; `jetty-cdi` installs the `CdiDecoratingListener` that Weld hooks into for servlet injection
+ Managed beans must have a default constructor and may not be `final` (must be proxiable)
+ Managed beans declaring a passivating scope must be passivation capable, 
implement `java.io.Serializable` and all `@Interceptors` must be Serializable as well

### Notes

+ CDI injection into Servlets, Filters and Listeners is enabled on Jetty 10 via
`jetty-cdi` + the `org.eclipse.jetty.cdi=CdiDecoratingListener` context init-param
+ Jetty 10 removed the `ServletContextHandler.Decorator` hook that older Weld/Jetty
auto-detection used; the init-param above is the Jetty 10 replacement
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
