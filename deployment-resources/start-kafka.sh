#!/bin/bash

cd $KAFKA_DIRECTORY

# Start Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties > /dev/null &

# Save PID
echo $! > ~/zookeeper.PID

sleep 3

# Start Kafka server
bin/kafka-server-start.sh config/server.properties > /dev/null &

# Save PID
echo $! > ~/kafka-broker.PID

echo Should have started now

