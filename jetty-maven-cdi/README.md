README
======

This is an simple setup for testing CDI ([Red Hat JBoss Weld]
(https://docs.jboss.org/weld/reference/latest/en-US/html/)) and Jetty using the 
[jetty-maven-plugin](http://www.eclipse.org/jetty/documentation/current/jetty-maven-plugin.html)

+ Jetty 9.4.57.v20241219
+ Weld 2.4.8.Final (CDI 1.2)

### Running this example Application

+ Check if Java 17 is used
+ Clone this git repository
+ Go to project directory, `java8-examples`
+ Execute `mvn clean install`
+ Go to root directory of this subproject, `jetty-maven-cdi`
+ Start Jetty and go to `http://localhost:8080/` to view the result in your browser

Weld 2.4 generates its client proxies with `ClassLoader.defineClass` via reflection, which
is not accessible by default on Java 17, so `jetty:run` needs
`MAVEN_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED"` (see the transcript below).

````bash
$ mvn --version
Apache Maven 3.6.3
Java version: 17.0.13, vendor: Ubuntu, runtime: /usr/lib/jvm/java-17-openjdk-amd64
OS name: "linux", version: "5.15.200", arch: "amd64", family: "unix"
$ git clone https://github.com/rmuller/java8-examples.git
Cloning into 'java8-examples'...
$ cd java8-examples/
$ mvn clean install
[INFO] Scanning for projects...
...
$ cd jetty-maven-cdi/
$ export MAVEN_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED"
$ mvn jetty:run
[INFO] Scanning for projects...
...
INFO: WELD-000900: 2.4.8 (Final)
INFO: WELD-ENV-001200: Jetty 7.2+ detected, CDI injection will be available in Servlets and Filters.
[INFO] Started ServerConnector@11bfffb3{HTTP/1.1, (http/1.1)}{0.0.0.0:8080}
[INFO] Started Jetty Server
````

### Jetty configuration

To enable CDI, you need to configure Jetty first. Several online resources describe the 
configuration in a different way. Most importantly the official Jetty and Weld documentation
are not consistent.

+ [Jetty documentation](http://www.eclipse.org/jetty/documentation/current/framework-weld.html)
+ [Weld documentation](https://docs.jboss.org/weld/reference/latest/en-US/html/environments.html)

### How to setup a CDI enabled application?

+ Add `javax.enterprise:cdi-api:1.2`, scope `provided` to your (maven) dependencies
+ Add `org.jboss.weld.servlet:weld-servlet:2.4.8.Final` as a dependency for
`jetty-maven-plugin`
+ Managed beans must have a default constructor and may not be `final` (must be proxiable)
+ Managed beans declaring a passivating scope must be passivation capable, 
implement `java.io.Serializable` and all `@Interceptors` must be Serializable as well

### Notes

+ CDI injection is available in 
    + Servlets and Filters (Jetty 7.2+)
    + Listeners (Jetty 9.1.1+)
+ [Jetty 9.1.0+ requires Weld 2.2.0+](https://issues.jboss.org/browse/WELD-1561)
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
