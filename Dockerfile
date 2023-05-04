#FROM docker-local.apps.calm-k8s.resolve.local/openjdk-11
FROM maven:3.6.3-openjdk-15

LABEL maintainer="Tonny <tonny@reslv.io>"

ENV JAVA_OPTS="" \
    WORKSPACE=/deployments \
	SERVER_PORT=8080

#RUN mkdir ${WORKSPACE}

COPY target/funki.loan-0.0.1-SNAPSHOT.jar ${WORKSPACE}/app.jar

WORKDIR ${WORKSPACE}

EXPOSE ${SERVER_PORT}

ENTRYPOINT ["/bin/sh", "-c", "java -XX:+UnlockExperimentalVMOptions -XX:+UseContainerSupport -Djava.security.egd=file:/dev/./urandom ${JAVA_OPTS} -jar ${WORKSPACE}/app.jar --server.port=${SERVER_PORT}"]