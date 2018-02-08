FROM jboss/base-jdk:8

USER root

RUN mkdir /opt/graphdb-benchmark/ && curl -Lo /opt/graphdb-benchmark/reviews_Kindle_Store_5.json.gz http://snap.stanford.edu/data/amazon/productGr$

USER jboss
ENTRYPOINT ["java", "-jar", "/opt/graphdb-benchmark/benchmark-jar-with-dependencies.jar"]

ARG APP_PATH

ADD $APP_PATH/target/benchmark-jar-with-dependencies.jar /opt/benchmark/