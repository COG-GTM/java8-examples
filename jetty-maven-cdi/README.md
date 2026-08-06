README
======

This is an simple setup for testing CDI ([Red Hat JBoss Weld]
(https://docs.jboss.org/weld/reference/latest/en-US/html/)) and Jetty using the 
[jetty-maven-plugin](https://jetty.org/docs/jetty/11/programming-guide/maven-jetty/jetty-maven-plugin.html)

+ Java 17
+ Jakarta EE 9.1 (Servlet 5.0, CDI 3.0)
+ Jetty 11.0.26
+ Weld 4.0.3.Final (CDI 3.0)

### Running this example Application

+ Check if Java 17 is used
+ Clone this git repository
+ Go to project directory, `java8-examples`
+ Execute `mvn clean install`
+ Go to root directory of this subproject, `jetty-maven-cdi`
+ Start Jetty and go to `http://localhost:8080/` to view the result in your browser

````bash
$ mvn --version
Apache Maven 3.6.3
Java version: 17.0.19, vendor: Ubuntu
$ git clone https://github.com/COG-GTM/java8-examples.git
Cloning into 'java8-examples'...
$ cd java8-examples/
$ mvn clean install
[INFO] Scanning for projects...
...
$ cd jetty-maven-cdi/
$ mvn jetty:run
[INFO] Scanning for projects...
...
[INFO] jetty-11.0.26; built: 2025-08-14T18:03:14.457Z; jvm 17.0.19+10
[INFO] CdiSpiDecorator enabled in ServletContext@o.e.j.m.p.MavenWebAppContext{/,...}
INFO: WELD-000900: 4.0.3 (Final)
INFO: WELD-ENV-001213: Jetty CDI SPI support detected, CDI injection will be available in
Listeners, Servlets and Filters.
[INFO] Started ServerConnector@1f7e52d1{HTTP/1.1, (http/1.1)}{0.0.0.0:8080}
[INFO] Started Server@72503b19{STARTING}[11.0.26,sto=0]
````

### Jetty configuration

To enable CDI, you need to configure Jetty first. Several online resources describe the 
configuration in a different way. Most importantly the official Jetty and Weld documentation
are not consistent.

+ [Jetty documentation](https://jetty.org/docs/jetty/11/programming-guide/index.html)
+ [Weld documentation](https://docs.jboss.org/weld/reference/latest/en-US/html/environments.html)

### How to setup a CDI enabled application?

+ Add `jakarta.enterprise:jakarta.enterprise.cdi-api:3.0.1`, scope `provided` and
`jakarta.servlet:jakarta.servlet-api:5.0.0`, scope `provided` to your (maven) dependencies
+ Add `org.jboss.weld.servlet:weld-servlet-shaded:4.0.3.Final`, scope `runtime`, so Weld is
deployed with the web application (it must be visible to the web application class loader)
+ Add `org.eclipse.jetty:jetty-cdi` as a dependency of the `jetty-maven-plugin`, so Jetty
installs its `CdiSpiDecorator` and injects Servlets, Filters and Listeners
+ Managed beans must have a default constructor and may not be `final` (must be proxiable)
+ Managed beans declaring a passivating scope must be passivation capable, 
implement `java.io.Serializable` and all `@Interceptors` must be Serializable as well

### Notes

+ Jetty 11 and Weld 4 use the `jakarta.*` namespace; the `javax.*` (Java EE) namespace is
only supported up to Jetty 10 / Weld 3
+ The Jetty 9 trick of exposing `ServletContextHandler.Decorator` to the web application by
removing it from the server classes (see `WEB-INF/jetty-context.xml`) is obsolete: the
`jetty-cdi` module now handles the decoration
+ The JNDI lookup of the `BeanManager` (bound by `WEB-INF/jetty-env.xml`) only resolves when
the application is deployed as a web archive, so use `mvn jetty:run-war` to see
`BeanManager JNDI lookup succeeded`. Weld's `ManagerObjectFactory` resolves the bean archive
by its `WEB-INF/classes` location, which does not exist when `mvn jetty:run` serves the
exploded project directory (`target/classes`)
+ Transactional events not available in a non-Java EE environment 

### References

+ [Jakarta Contexts and Dependency Injection 3.0](https://jakarta.ee/specifications/cdi/3.0/)
+ [Jakarta Servlet 5.0](https://jakarta.ee/specifications/servlet/5.0/)
+ [Weld - CDI: Contexts and Dependency Injection for the Jakarta EE platform]
(https://docs.jboss.org/weld/reference/latest/en-US/html/index.html)
+ [Introduction to JNDI](http://archive.oreilly.com/pub/a/onjava/excerpt/java_servlets_ch12/index.html?page=3)
+ [Working with Jetty JNDI](https://jetty.org/docs/jetty/11/programming-guide/server/jndi.html)
