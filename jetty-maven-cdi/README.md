README
======

This is an simple setup for testing CDI ([Red Hat JBoss Weld]
(https://docs.jboss.org/weld/reference/latest/en-US/html/)) and Jetty using the 
[jetty-maven-plugin](http://www.eclipse.org/jetty/documentation/current/jetty-maven-plugin.html)

+ Jetty 10.0.20 (`javax.*` Servlet 4.0)
+ Weld 3.1.9.Final (CDI 2.0)

### Running this example Application

+ Check if Java 17 (LTS) is used
+ Clone this git repository
+ Go to project directory, `java8-examples`
+ Execute `mvn clean install`
+ Go to root directory of this subproject, `jetty-maven-cdi`
+ Start Jetty and go to `http://localhost:8080/` to view the result in your browser

````bash
$ mvn --version
Apache Maven 3.6.3
Maven home: /usr/share/maven
Java version: 17.0.13, vendor: Ubuntu
Java home: /usr/lib/jvm/java-17-openjdk-amd64
Default locale: en, platform encoding: UTF-8
OS name: "linux", version: "5.15.200", arch: "amd64", family: "unix"
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
$ mvn jetty:run
[INFO] Scanning for projects...
[INFO]                                                                         
[INFO] ------------------------------------------------------------------------
[INFO] Building jetty-maven-cdi 1.0.0-SNAPSHOT
[INFO] ------------------------------------------------------------------------
...
[INFO] Started ServerConnector@beabd6b{HTTP/1.1, (http/1.1)}{0.0.0.0:8080}
[INFO] Started Server@e38f0b7{STARTING}[10.0.20,sto=0] @1634ms
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
+ Add `org.jboss.weld.servlet:weld-servlet-shaded:3.1.9.Final`, scope `runtime`, so Weld is
deployed with the web application (its `ServletContainerInitializer` bootstraps CDI)
+ Add `org.eclipse.jetty:jetty-cdi` as a dependency of `jetty-maven-plugin` (container
classpath) and enable it from `jetty-context.xml` by setting the context attribute
`org.eclipse.jetty.cdi` to `CdiSpiDecorator`
+ Managed beans must have a default constructor and may not be `final` (must be proxiable)
+ Managed beans declaring a passivating scope must be passivation capable, 
implement `java.io.Serializable` and all `@Interceptors` must be Serializable as well

### Notes

+ CDI injection is available in 
    + Servlets and Filters (Jetty 7.2+)
    + Listeners (Jetty 9.1.1+)
+ [Jetty 9.1.0+ requires Weld 2.2.0+](https://issues.jboss.org/browse/WELD-1561)
+ Transactional events not available in a non-Java EE environment
+ With Jetty 10 + Weld 3, injection of the `BeanManager` works, but looking it up from JNDI
(`java:comp/BeanManager`, `java:comp/env/BeanManager`) does not: Weld 3's
`ManagerObjectFactory` cannot resolve the container id and reports
`WELD-001300: Unable to locate BeanManager`. Use injection or `CDI.current().getBeanManager()`. 

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
