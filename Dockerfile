FROM jboss/base-jdk:8

USER root

RUN mkdir /opt/graphdb-benchmark/ \
    && curl -Lo /opt/graphdb-benchmark/reviews_Kindle_Store_5.json.gz http://snap.stanford.edu/data/amazon/productGraph/categoryFiles/reviews_Kindle_Store_5.json.gz \
    && chmod 777 /opt/graphdb-benchmark/reviews_Kindle_Store_5.json.gz

RUN curl -Lo /opt/graphdb-benchmark/meta_Kindle_Store.json.gz http://snap.stanford.edu/data/amazon/productGraph/categoryFiles/meta_Kindle_Store.json.gz \
        && chmod 777 /opt/graphdb-benchmark/meta_Kindle_Store.json.gz

USER jboss
ENTRYPOINT ["java", "-jar", "/opt/graphdb-benchmark/benchmark-jar-with-dependencies.jar"]

ARG APP_PATH

ADD $APP_PATH/target/benchmark-jar-with-dependencies.jar /opt/graphdb-benchmark/