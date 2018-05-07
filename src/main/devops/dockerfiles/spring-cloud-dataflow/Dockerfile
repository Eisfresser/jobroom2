FROM openjdk:8-jre-alpine

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JHIPSTER_SLEEP=0 \
    JAVA_OPTS="" \
    TMP=/tmp/spring-flow/logs

# Download spring cloud data flow server
# Version 1.3.1 seems to be broken, as it doesn't deploy the runtime properties for streams and tasks
# RUN wget -O spring-cloud-dataflow-server-local.jar http://repo.spring.io/release/org/springframework/cloud/spring-cloud-dataflow-server-local/1.3.0.RELEASE/spring-cloud-dataflow-server-local-1.3.0.RELEASE.jar
RUN wget -O spring-cloud-dataflow-server-local.jar https://alvch.jfrog.io/alvch/libs-releases/org/springframework/cloud/spring-cloud-dataflow-server-local/1.4.0.FIXED/spring-cloud-dataflow-server-local-1.4.0.FIXED.jar

# Path for logs
RUN mkdir -p ${TMP}

VOLUME ${TMP}

# Port range for streams
EXPOSE 7000-7010 9393

# Run application
CMD echo "The application will start in ${JHIPSTER_SLEEP}s..." && \
    sleep ${JHIPSTER_SLEEP} && \
    java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -Djava.io.tmpdir=${TMP} \
    -jar /spring-cloud-dataflow-server-local.jar \
    --spring.config.location=file:/run/secrets/ \
    --spring.application.name=spring-cloud-dataflow-server-local \
    --logging.level.org.springframework.cloud.deployer=TRACE
