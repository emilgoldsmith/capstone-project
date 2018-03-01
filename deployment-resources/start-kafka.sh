#!/bin/bash

if [[ $# -ne 1 ]];
then
  echo "Must supply topic name argument"
  exit 1
fi

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

sleep 5
# Create topic from commandline arguments
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic $1 > /dev/null

echo Should have started now and created topic

