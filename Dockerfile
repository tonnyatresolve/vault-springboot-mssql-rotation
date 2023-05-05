### Build Stage ###

## FROM docker-local.apps.calm-k8s.resolve.local/openjdk-11
FROM maven:3.8.2-openjdk-17 AS build

LABEL maintainer="Tonny <tonny@reslv.io>"

COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

## RUN ls -lrt /home/app
## RUN ls -lrt /home/app/target

### Package Stage ###

FROM openjdk:17-jdk-slim
## FROM maven:3.8.2-openjdk-17

USER root

ENV JAVA_OPTS="" \
    WORKSPACE=/deployments \
    SERVER_PORT=8080

RUN mkdir ${WORKSPACE}

COPY demo.reslv.one_ca.pem $JAVA_HOME/lib/security 

RUN echo ${JAVA_HOME}
RUN ls -lrt ${JAVA_HOME}/lib/security

RUN \
    cd $JAVA_HOME/lib/security \
    && keytool -keystore cacerts -storepass changeit -noprompt -trustcacerts -importcert -alias demo_cert -file demo.reslv.one_ca.pem 
##    && keytool -cacerts -storepass changeit -noprompt -trustcacerts -importcert -alias demo_cert -file $JAVA_HOME/lib/security/cacerts/demo.reslv.one_ca.pem 

COPY --from=build /home/app/target/vault-springboot-mssql-rotation-0.0.1-SNAPSHOT.jar ${WORKSPACE}/app.jar
## COPY /home/app/target/funki.loan-0.0.1-SNAPSHOT.jar ${WORKSPACE}/app.jar

WORKDIR ${WORKSPACE}

EXPOSE ${SERVER_PORT}

ENTRYPOINT ["/bin/sh", "-c", "java -XX:+UnlockExperimentalVMOptions -XX:+UseContainerSupport -Djava.security.egd=file:/dev/./urandom ${JAVA_OPTS} -jar ${WORKSPACE}/app.jar --server.port=${SERVER_PORT}"]
