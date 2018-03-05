#!/usr/bin/env bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
APP_NAME="${1:?Need to set APP_NAME parameter (1th argument) to a non-empty value}"

case $APP_NAME in
    janusgraph|orientDb|tinker|neo4j|postgres)
        echo "Building $APP_NAME"
    ;;
    *)
        echo "App not found: $APP_NAME"
    ;;
esac

mvn clean install assembly:single

if [ $? -ne 0 ]; then
    return $?
fi

docker build --build-arg APP_PATH="$APP_NAME" -t eaybars/"$APP_NAME"-benchmark .