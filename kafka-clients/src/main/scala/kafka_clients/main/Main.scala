package main;

import kafka_clients.consumer.Consumer;
import kafka_clients.producer.Producer;

import java.util.Observer;
import java.util.Observable;
import java.lang.Object;

object Main extends Observer {
  var hostAndPort: String = "";
  var topicName: String = "";
  var groupId: String = "";
  var consumer: Consumer = null;
  var producer: Producer = null;

  def main(args: Array[String]) {
    if (args.length != 3) {
      println("Usage: ./binary <host:port> <topicName> <groupId>\n");
      System.exit(1);
    }

    this.hostAndPort = args(0);
    this.topicName = args(1);
    this.groupId = args(2);
    this.consumer = new Consumer(hostAndPort, topicName, groupId);
    this.consumer.addObserver(this);
    this.producer = new Producer(hostAndPort, topicName, groupId);
    Thread sleep 5 * 1000;
    this.producer.send("It's working!");
  }

  def update(observable: Observable, message: Object) {
    println(message);
  }
}
