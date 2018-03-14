#!/usr/bin/env bash

CASSANDRA_STARTUP_TIMEOUT_S=${CASSANDRA_STARTUP_TIMEOUT_S:=60}
ELASTICSEARCH_STARTUP_TIMEOUT_S=${ELASTICSEARCH_STARTUP_TIMEOUT_S:=60}
SLEEP_INTERVAL_S=${SLEEP_INTERVAL_S:=2}
ELASTICSEARCH_STARTUP_TIMEOUT_S=${ELASTICSEARCH_STARTUP_TIMEOUT_S:=60}

BIN=/opt/janusgraph/bin

wait_for_cassandra() {
    local now_s=`date '+%s'`
    local stop_s=$(( $now_s + $CASSANDRA_STARTUP_TIMEOUT_S ))
    local status_thrift=

    echo -n 'Running `nodetool statusthrift`'
    while [ $now_s -le $stop_s ]; do
        echo -n .
        # The \r\n deletion bit is necessary for Cygwin compatibility
        status_thrift="`$BIN/nodetool statusthrift 2>/dev/null | tr -d '\n\r'`"
        if [ $? -eq 0 -a 'running' = "$status_thrift" ]; then
            echo ' OK (returned exit status 0 and printed string "running").'
            return 0
        fi
        sleep $SLEEP_INTERVAL_S
        now_s=`date '+%s'`
    done

    echo " timeout exceeded ($CASSANDRA_STARTUP_TIMEOUT_S seconds)" >&2
    return 1
}

wait_for_elasticsearch() {
    wait_for_startup "Elasticsearch" 127.0.0.1 9200 $ELASTICSEARCH_STARTUP_TIMEOUT_S || {
        echo "Elasticsearch is unavailable. Check Elasticsearch log output."  >&2
        return 1
    }
}

# wait_for_startup friendly_name host port timeout_s
wait_for_startup() {
    local friendly_name="$1"
    local host="$2"
    local port="$3"
    local timeout_s="$4"

    local now_s=`date '+%s'`
    local stop_s=$(( $now_s + $timeout_s ))
    local status=

    echo -n "Connecting to $friendly_name ($host:$port)"
    while [ $now_s -le $stop_s ]; do
        echo -n .
        $BIN/checksocket.sh $host $port >/dev/null 2>&1
        if [ $? -eq 0 ]; then
            echo " OK (connected to $host:$port)."
            return 0
        fi
        sleep $SLEEP_INTERVAL_S
        now_s=`date '+%s'`
    done

    echo " timeout exceeded ($timeout_s seconds): could not connect to $host:$port" >&2
    return 1
}

wait_for_cassandra && wait_for_elasticsearch && exec /opt/janusgraph/bin/gremlin-server.sh