README
======

This is an simple setup for testing CDI ([Red Hat JBoss Weld]
(https://docs.jboss.org/weld/reference/latest/en-US/html/)) and Jetty using the 
[jetty-maven-plugin](http://www.eclipse.org/jetty/documentation/current/jetty-maven-plugin.html)

+ Java 17 (LTS)
+ Jetty 9.4.58.v20250814 (`jetty-maven-plugin` + `jetty-cdi`)
+ Weld 3.1.9.Final (`weld-servlet-shaded`, CDI 2.0, `javax.*` namespace)

### Running this example Application

+ Check if Java 17 is used
+ Clone this git repository
+ Go to project directory, `java8-examples`
+ Execute `mvn clean install`
+ Go to root directory of this subproject, `jetty-maven-cdi`
+ Start Jetty and go to `http://localhost:8080/` to view the result in your browser

````bash
$ java -version
openjdk version "17.0.20.1" 2026-08-18
OpenJDK Runtime Environment (build 17.0.20.1+1-1-22.04-Ubuntu)
OpenJDK 64-Bit Server VM (build 17.0.20.1+1-1-22.04-Ubuntu, mixed mode, sharing)
$ mvn --version
Apache Maven 3.6.3
Maven home: /usr/share/maven
Java version: 17.0.20.1, vendor: Ubuntu, runtime: /usr/lib/jvm/java-17-openjdk-amd64
...
$ git clone https://github.com/COG-GTM/java8-examples.git
Cloning into 'java8-examples'...
$ cd java8-examples/
$ mvn clean install
[INFO] Scanning for projects...
...
[INFO] Compiling 3 source files with javac [debug release 17] to target/classes
...
[INFO] BUILD SUCCESS
$ cd jetty-maven-cdi/
$ mvn jetty:run
[INFO] Scanning for projects...
...
[INFO] jetty-9.4.58.v20250814; ... jvm 17.0.20.1+1-1-22.04-Ubuntu
...
INFO: WELD-000900: 3.1.9 (Final)
INFO: WELD-ENV-001212: Jetty CdiDecoratingListener support detected, CDI injection will be available in Listeners, Servlets and Filters.
...
[INFO] Started Jetty Server
````

### Jetty configuration

To enable CDI, you need to configure Jetty first. Several online resources describe the 
configuration in a different way. Most importantly the official Jetty and Weld documentation
are not consistent.

+ [Jetty documentation](http://www.eclipse.org/jetty/documentation/current/framework-weld.html)
+ [Weld documentation](https://docs.jboss.org/weld/reference/latest/en-US/html/environments.html)

### How to setup a CDI enabled application?

+ Add `javax.enterprise:cdi-api:2.0.SP1`, scope `provided` to your (maven) dependencies
+ Add `org.jboss.weld.servlet:weld-servlet-shaded:3.1.9.Final`, scope `runtime`, to your
(maven) dependencies (Weld is deployed in `WEB-INF/lib`)
+ Add `org.eclipse.jetty:jetty-cdi` as a dependency for `jetty-maven-plugin` and set the
context init parameter `org.eclipse.jetty.cdi=CdiDecoratingListener` (see `jetty-context.xml`)
+ Managed beans must have a default constructor and may not be `final` (must be proxiable)
+ Managed beans declaring a passivating scope must be passivation capable, 
implement `java.io.Serializable` and all `@Interceptors` must be Serializable as well

### Notes

+ CDI injection is available in 
    + Servlets and Filters (Jetty 7.2+)
    + Listeners (Jetty 9.1.1+)
+ [Jetty 9.1.0+ requires Weld 2.2.0+](https://issues.jboss.org/browse/WELD-1561)
+ Jetty 9.4.20+ integrates CDI via `jetty-cdi` (`CdiDecoratingListener`), supported by Weld 3.1.2+
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
