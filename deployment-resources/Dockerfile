FROM ubuntu:16.04

RUN apt update

# Install Java

RUN apt install -y openjdk-8-jdk

# Install a few needed dependencies

RUN apt install -y wget

RUN apt install -y zip

# Install Kafka

RUN wget -O kafka.tgz http://www-eu.apache.org/dist/kafka/1.0.0/kafka_2.12-1.0.0.tgz

RUN tar -xvzf kafka.tgz

RUN rm -rf kafka.tgz

ENV KAFKA_DIRECTORY /kafka_2.12-1.0.0

# Install SBT

RUN wget -O sbt.tgz https://github.com/sbt/sbt/releases/download/v1.1.0/sbt-1.1.0.tgz

RUN tar -xvzf sbt.tgz

RUN mv sbt /.sbt && rm -rf sbt.tgz

RUN ln -s /.sbt/bin/sbt /bin/

# Create dummy file to force sbt to install Scala version while Docker can cache it

RUN echo "scalaVersion := \"2.12.4\"" > build.sbt

RUN sbt -sbt-version 1.1.0 compile

RUN rm -rf build.sbt target

# Get Github repo last so it doesn't mess with the caching above

ADD https://github.com/emilgoldsmith/capstone-project/archive/master.zip github-repo.zip

RUN unzip github-repo.zip && rm -rf github-repo.zip

WORKDIR /capstone-project-master

# Build kafka clients

RUN cd kafka-clients && sbt stage

# Setup useful symlinks

RUN ln -s ./kafka-clients/target/universal/stage/bin/consumer consumer

RUN ln -s ./kafka-clients/target/universal/stage/bin/producer producer

# Expose ports needed for Kafka

EXPOSE 2181 9092
