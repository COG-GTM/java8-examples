# Multi-stage build for the jetty-maven-cdi example.
#
# Build context must be the repository root so the parent POM and Maven Wrapper
# are available, e.g.:
#   docker build -f jetty-maven-cdi/Dockerfile -t jetty-maven-cdi .

# ---- Stage 1: build the WAR with the Maven Wrapper on JDK 21 ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY . .
RUN ./mvnw -B -pl jetty-maven-cdi -am clean package

# ---- Stage 2: run the WAR on the official Jetty 12 (EE10) runtime ----
FROM jetty:12.0-jre21
USER root
# Enable the Jakarta EE 10 deployment + annotation scanning modules.
RUN java -jar "$JETTY_HOME/start.jar" --approve-all-licenses \
        --add-modules=server,http,ee10-deploy,ee10-annotations
# Deploy the WAR as the ROOT context so it is served at http://localhost:8080/
COPY --from=build /workspace/jetty-maven-cdi/target/jetty-maven-cdi-*.war \
        /var/lib/jetty/webapps/ROOT.war
USER jetty
EXPOSE 8080
