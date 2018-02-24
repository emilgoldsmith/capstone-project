#!/bin/bash

KAFKA_SERVER_PID=$(cat $KAFKA_DIRECTORY/../.kafka-broker.PID)
ZOOKEEPER_PID=$(cat $KAFKA_DIRECTORY/../.zookeeper.PID)

kill -s TERM $KAFKA_SERVER_PID

# Wait for the kafka server to finish
tail --pid=$KAFKA_SERVER_PID -f /dev/null

kill -s TERM $ZOOKEEPER_PID

echo Successfully killed
