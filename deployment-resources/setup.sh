#!/bin/bash

cd ~

sudo apt update

# Install Java

sudo apt install -y openjdk-8-jdk

# Install a few needed dependencies

sudo apt install -y wget

sudo apt install -y zip

# Install Kafka

wget -O kafka.tgz http://www-eu.apache.org/dist/kafka/1.0.0/kafka_2.12-1.0.0.tgz

tar -xvzf kafka.tgz

rm -rf kafka.tgz

export KAFKA_DIRECTORY=~/kafka_2.12-1.0.0

echo "export KAFKA_DIRECTORY=~/kafka_2.12-1.0.0" >> .bashrc

# Install SBT

wget -O sbt.tgz https://github.com/sbt/sbt/releases/download/v1.1.0/sbt-1.1.0.tgz

tar -xvzf sbt.tgz

mv sbt ~/.sbt && rm -rf sbt.tgz

sudo ln -s ~/.sbt/bin/sbt /bin/

# Get Github repo last so it doesn't mess with the caching above

wget -O github-repo.zip https://github.com/emilgoldsmith/capstone-project/archive/master.zip

unzip github-repo.zip && rm -rf github-repo.zip

cd capstone-project-master

# Build kafka clients

cd kafka-clients && sbt stage && cd ..

# Setup useful symlinks

ln -s ./kafka-clients/target/universal/stage/bin/consumer consumer

ln -s ./kafka-clients/target/universal/stage/bin/producer producer
