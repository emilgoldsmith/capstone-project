FROM ubuntu:16.04

RUN apt update

RUN apt install -y openjdk-8-jdk

RUN apt install -y wget

RUN apt install -y zip

ADD https://github.com/emilgoldsmith/capstone-project/archive/master.zip github-repo.zip

RUN unzip github-repo.zip && rm -rf github-repo.zip

WORKDIR /capstone-project-master

RUN wget -O kafka.tgz http://www-eu.apache.org/dist/kafka/1.0.0/kafka_2.12-1.0.0.tgz

RUN tar -xvzf kafka.tgz

RUN mv kafka_2.12-1.0.0 .kafka

ENV KAFKA_DIRECTORY /capstone-project-master/.kafka

RUN rm -rf kafka.tgz

EXPOSE 2181 9092