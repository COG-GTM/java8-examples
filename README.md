README
======

This repository contains simple example projects of various Java 8 based projects.

The examples now build and run on Java 17 (LTS) with the following software stack:

+ Ubuntu 22.04 LTS
+ OpenJDK 17
+ Apache Maven 3.6+

The build compiles with `--release 17`. JVM flags needed at runtime (e.g. `--add-opens`
for Weld proxy generation) are declared in `.mvn/jvm.config`.

Originally developed and tested with Ubuntu 14.04.1 LTS, Oracle JDK 1.8.0_25,
NetBeans IDE 8.0.2 and Apache Maven 3.2.2.

