FROM jboss/base-jdk:8

ENV JANUS_VERSION 0.2.0

USER root

RUN curl -Lo /opt/janusgraph.zip https://github.com/JanusGraph/janusgraph/releases/download/v"$JANUS_VERSION"/janusgraph-"$JANUS_VERSION"-hadoop2.zip && \
    unzip /opt/janusgraph.zip -d /opt/ && \
    rm /opt/janusgraph.zip && \
    mv /opt/janusgraph-"$JANUS_VERSION"-hadoop2 opt/janusgraph && \
    chmod 777 opt/janusgraph/bin/gremlin-server.sh

USER jboss

ENTRYPOINT ["opt/janusgraph/bin/gremlin-server.sh"]
