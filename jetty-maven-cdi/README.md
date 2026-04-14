README
======

This is a simple setup for testing CDI ([Red Hat JBoss Weld]
(https://docs.jboss.org/weld/reference/latest/en-US/html/)) and Jetty using the 
[jetty-ee10-maven-plugin](https://eclipse.dev/jetty/documentation/jetty-12/programming-guide/index.html)

+ Jetty 12.0.14 (EE10)
+ Weld 5.1.2.Final (CDI 4.0 / Jakarta namespace)

### Running this example Application

+ Check if Java 21 is used
+ Clone this git repository
+ Go to project directory, `java8-examples`
+ Execute `mvn clean install`
+ Go to root directory of this subproject, `jetty-maven-cdi`
+ Start Jetty and go to `http://localhost:8080/` to view the result in your browser

````bash
$ mvn --version
Apache Maven 3.9.x
Java version: 21, vendor: Oracle Corporation (or equivalent)
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", arch: "amd64", family: "unix"
$ git clone https://github.com/COG-GTM/java8-examples.git
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
$ mvn jetty:run
[INFO] Scanning for projects...
[INFO]                                                                         
[INFO] ------------------------------------------------------------------------
[INFO] Building jetty-maven-cdi 1.0.0-SNAPSHOT
[INFO] ------------------------------------------------------------------------
...
2014-12-26 15:49:20.915:INFO:oejs.ServerConnector:main: Started ServerConnector@2a685eba{HTTP/1.1}{0.0.0.0:8080}
2014-12-26 15:49:20.916:INFO:oejs.Server:main: Started @3828ms
[INFO] Started Jetty Server
````

### Jetty configuration

To enable CDI, you need to configure Jetty first. Several online resources describe the 
configuration in a different way. Most importantly the official Jetty and Weld documentation
are not consistent.

+ [Jetty documentation](http://www.eclipse.org/jetty/documentation/current/framework-weld.html)
+ [Weld documentation](https://docs.jboss.org/weld/reference/latest/en-US/html/environments.html)

### How to setup a CDI enabled application?

+ Add `jakarta.enterprise:jakarta.enterprise.cdi-api:4.0.1`, scope `provided` to your (maven) dependencies
+ Add `org.jboss.weld.servlet:weld-servlet-shaded:5.1.2.Final` as a dependency for
`jetty-ee10-maven-plugin`
+ Managed beans must have a default constructor and may not be `final` (must be proxiable)
+ Managed beans declaring a passivating scope must be passivation capable, 
implement `java.io.Serializable` and all `@Interceptors` must be Serializable as well

### Notes

+ CDI injection is available in 
    + Servlets and Filters
    + Listeners
+ Jetty 12 EE10 requires Weld 5.x+ for CDI 4.0 / Jakarta namespace support
+ Transactional events not available in a non-Java EE environment 

### References

+ [CDI 4.0 (Jakarta Contexts and Dependency Injection)](https://jakarta.ee/specifications/cdi/4.0/)
+ [Jakarta EE 10 Specification](https://jakarta.ee/specifications/platform/10/)
+ [Weld 5.x - CDI Reference Implementation](https://docs.jboss.org/weld/reference/latest/en-US/html/index.html)
+ [Jetty 12 Documentation](https://eclipse.dev/jetty/documentation/jetty-12/index.html)
+ [Working with Jetty JNDI](https://eclipse.dev/jetty/documentation/jetty-12/programming-guide/index.html)
