FROM jboss/base-jdk:8

USER root

RUN mkdir /opt/graphdb-benchmark/ \
    && curl -Lo /opt/graphdb-benchmark/reviews_Kindle_Store_5.json.gz http://snap.stanford.edu/data/amazon/productGraph/categoryFiles/reviews_Kindle_Store_5.json.gz \
    && chmod 777 /opt/graphdb-benchmark/reviews_Kindle_Store_5.json.gz

RUN curl -Lo /opt/graphdb-benchmark/meta_Kindle_Store.json.gz http://snap.stanford.edu/data/amazon/productGraph/categoryFiles/meta_Kindle_Store.json.gz \
        && chmod 777 /opt/graphdb-benchmark/meta_Kindle_Store.json.gz

ENV JAVA_OPTIONS "-Xms256m -Xmx512m"

USER jboss
ENTRYPOINT arg=($JAVA_OPTIONS) \
	&& arg+=("-jar" "/opt/graphdb-benchmark/benchmark-jar-with-dependencies.jar") \
	&& arg+=("$@") \
	&& java ${arg[@]}

ARG APP_PATH

ADD $APP_PATH/target/benchmark-jar-with-dependencies.jar /opt/graphdb-benchmark/