package group_by.simple;

import kafka_clients.Consumer;
import kafka_clients.Producer;
import kafka_clients.Admin;

import java.util.Observer;
import java.util.Observable;
import java.util.Collection;

import java.lang.Object;

import scala.collection.JavaConverters._;
import scala.collection.JavaConversions._;

object Master extends Observer {
  var hostAndPort: String = "";
  var topicName: String = "";
  var groupId: String = "";
  var consumer: Consumer = null;
  var producer: Producer = null;
  var admin: Admin = null;
  var numNodes: Int = 0;
  var nodes: List[String] = List();

  def main(args: Array[String]) {
    if (args.length != 2) {
      println("Usage: ./binary <host:port>\n");
      System.exit(1);
    }

    this.hostAndPort = args(0);
    this.numNodes = args(1).toInt;

    this.admin = new Admin(this.hostAndPort);
    this.admin.createTopics(List("master", "all-workers").asJava);
    this.consumer = new Consumer(this.hostAndPort, List("master").asJava, "master-group");
    this.consumer.addObserver(this);
    this.producer = new Producer(this.hostAndPort, "all-workers");
  }

  def update(observable: Observable, arg: Object) {
    val message: String = arg.asInstanceOf[String];
    if (message.indexOf("connecting:") == 0) {
      val id = message.substring("connecting:".length());
      this.nodes = this.nodes :+ id;
      if (this.nodes.length == this.numNodes) {
        this.producer.send("start");
      }
    }
  }
}
