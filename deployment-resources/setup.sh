#!/bin/bash

cd ~

sudo apt update

if [[ $? -ne 0 && $? -ne 100 ]];
then
  echo "Error updating" >&2
  exit 1
fi

# Install Java

sudo apt install -y openjdk-8-jdk

if [[ $? -ne 0 ]];
then
  echo "Error installing Java" >&2
  exit 1
fi

# Install a few needed dependencies

sudo apt install -y wget

if [[ $? -ne 0 ]];
then
  echo "Error installing wget" >&2
  exit 1
fi

sudo apt install -y zip

if [[ $? -ne 0 ]];
then
  echo "Error installing zip" >&2
  exit 1
fi

# Install Kafka

wget -O kafka.tgz http://www-eu.apache.org/dist/kafka/1.0.0/kafka_2.12-1.0.0.tgz

if [[ $? -ne 0 ]];
then
  echo "Error fetching kafka" >&2
  exit 1
fi

tar -xvzf kafka.tgz

if [[ $? -ne 0 ]];
then
  echo "Error unzipping kafka" >&2
  exit 1
fi

rm -rf kafka.tgz

if [[ $? -ne 0 ]];
then
  echo "Error removing kafka zip" >&2
  exit 1
fi

export KAFKA_DIRECTORY=~/kafka_2.12-1.0.0

echo "export KAFKA_DIRECTORY=~/kafka_2.12-1.0.0" >> .bashrc

# Install SBT

wget -O sbt.tgz https://github.com/sbt/sbt/releases/download/v1.1.0/sbt-1.1.0.tgz

if [[ $? -ne 0 ]];
then
  echo "Error getting sbt" >&2
  exit 1
fi

tar -xvzf sbt.tgz

if [[ $? -ne 0 ]];
then
  echo "Error unzipping sbt" >&2
  exit 1
fi

mv sbt ~/.sbt && rm -rf sbt.tgz

if [[ $? -ne 0 ]];
then
  echo "Error moving sbt" >&2
  exit 1
fi

sudo ln -s ~/.sbt/bin/sbt /bin/

if [[ $? -ne 0 ]];
then
  echo "Error setting up sbt binary path" >&2
  exit 1
fi

# Get Github repo last so it doesn't mess with the caching above

sudo apt install -y git

if [[ $? -ne 0 ]];
then
  echo "Error installing git" >&2
  exit 1
fi

git clone https://github.com/emilgoldsmith/capstone-project.git

if [[ $? -ne 0 ]];
then
  echo "Error getting repo" >&2
  exit 1
fi

cd capstone-project

# Build kafka clients

cd main && sbt stage && cd ..

if [[ $? -ne 0 ]];
then
  echo "Error building kafka clients" >&2
  exit 1
fi

# Setup useful symlinks

ln -s ./main/target/universal/stage/bin/master master

if [[ $? -ne 0 ]];
then
  echo "Error setting up symlinks" >&2
  exit 1
fi

ln -s ./main/target/universal/stage/bin/worker worker

if [[ $? -ne 0 ]];
then
  echo "Error setting up symlinks 2" >&2
  exit 1
fi
