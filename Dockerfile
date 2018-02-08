FROM jboss/base-jdk:8


RUN curl -Lo /opt/graphdb-benchmark/reviews_Kindle_Store_5.json.gz http://snap.stanford.edu/data/amazon/productGraph/categoryFiles/reviews_Kindle_Store_5.json.gz

ENTRYPOINT ["java", "-jar", "/opt/graphdb-benchmark/benchmark-jar-with-dependencies.jar"]

ARG APP_PATH

ADD $APP_PATH/target/benchmark-jar-with-dependencies.jar /opt/benchmark/

