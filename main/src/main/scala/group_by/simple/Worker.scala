package group_by.simple;

import kafka_clients.Consumer;
import kafka_clients.Producer;

import java.util.Observer;
import java.util.Observable;
import java.util.Collection;

import java.lang.Object;

import scala.collection.JavaConverters._;
import scala.collection.JavaConversions._;

object Worker extends Observer {
  var hostAndPort: String = "";
  var groupId: String = "";
  var consumer: Consumer = null;
  var producer: Producer = null;
  var allWorkers: Array[String] = Array();

  def main(args: Array[String]) {
    if (args.length != 2) {
      println("Usage: ./binary <host:port> <groupId>\n");
      System.exit(1);
    }

    this.hostAndPort = args(0);
    this.groupId = args(1);

    this.consumer = new Consumer(this.hostAndPort, List("all-workers").asJava, this.groupId);
    this.consumer.addObserver(this);
    this.producer = new Producer(this.hostAndPort, "master");
    this.producer.send(s"connecting:${this.groupId}");
  }

  def update(observable: Observable, arg: Object) {
    val message: String = arg.asInstanceOf[String];
    println(message);
    val splitString: Array[String] = message.split(" ");
    if (splitString.length > 0 && splitString(0) == "start") {
      splitString.slice(1, splitString.length).foreach { x => this.allWorkers = this.allWorkers :+ x };
    }
  }
}
