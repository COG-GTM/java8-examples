README
======

This is a simple setup for testing CDI ([Red Hat JBoss Weld]
(https://docs.jboss.org/weld/reference/latest/en-US/html/)) and Jetty using the 
[jetty-maven-plugin](https://eclipse.dev/jetty/documentation/jetty-11/programming-guide/index.html)

+ Jetty 11.0.24
+ Weld 5.1.2.Final (CDI 4.0, Jakarta EE 9+)

### Running this example Application

+ Check if Java 17 is used
+ Clone this git repository
+ Go to project directory, `java8-examples`
+ Execute `mvn clean install`
+ Go to root directory of this subproject, `jetty-maven-cdi`
+ Start Jetty and go to `http://localhost:8080/` to view the result in your browser

````bash
$ mvn --version
Apache Maven 3.9.9
Maven home: /usr/share/maven
Java version: 17.0.x, vendor: Eclipse Adoptium
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "...", arch: "amd64", family: "unix"
$ git clone https://github.com/COG-GTM/java8-examples.git
Cloning into 'java8-examples'...
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
[INFO] Started Jetty Server
````

### Jetty configuration

To enable CDI, you need to configure Jetty first. Several online resources describe the 
configuration in a different way. Most importantly the official Jetty and Weld documentation
are not consistent.

+ [Jetty documentation](https://eclipse.dev/jetty/documentation/jetty-11/operations-guide/index.html)
+ [Weld documentation](https://docs.jboss.org/weld/reference/latest/en-US/html/environments.html)

### How to setup a CDI enabled application?

+ Add `jakarta.enterprise:jakarta.enterprise.cdi-api:3.0.1`, scope `provided` to your (maven) dependencies
+ Add `org.jboss.weld.servlet:weld-servlet-shaded:5.1.2.Final` as a dependency for
`jetty-maven-plugin`
+ Managed beans must have a default constructor and may not be `final` (must be proxiable)
+ Managed beans declaring a passivating scope must be passivation capable, 
implement `java.io.Serializable` and all `@Interceptors` must be Serializable as well

### Notes

+ CDI injection is available in 
    + Servlets and Filters (Jetty 7.2+)
    + Listeners (Jetty 9.1.1+)
+ Jetty 11 has first-class CDI support via the `jetty-cdi` module
+ Transactional events not available in a non-Jakarta EE environment 

### References

+ [Jakarta Contexts and Dependency Injection](https://jakarta.ee/specifications/cdi/)
+ [Jakarta EE Tutorial, Contexts and Dependency Injection]
(https://jakarta.ee/learn/docs/jakartaee-tutorial/current/cdi/cdi-basic/cdi-basic.html)
+ [Weld - CDI: Contexts and Dependency Injection]
(https://docs.jboss.org/weld/reference/latest/en-US/html/index.html)
+ [Working with Jetty JNDI](https://eclipse.dev/jetty/documentation/jetty-11/operations-guide/index.html)
