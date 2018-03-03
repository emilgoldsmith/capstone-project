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
import scala.collection.mutable.Map;

object Master extends Observer {
  var hostAndPort: String = "";
  var consumer: Consumer = null;
  var producer: Producer = null;
  var admin: Admin = null;
  var numNodes: Int = 0;
  var updatesReceived: Int = 0;
  var nodes: List[String] = List();
  var results: Map[Int, (Double, Int)] = Map[Int, (Double, Int)]();

  def main(args: Array[String]) {
    if (args.length != 2) {
      println("Usage: ./binary <host:port> <numNodes>\n");
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
        this.nodes.foreach { println };
        var startCommand: StringBuilder = new StringBuilder;
        startCommand ++= "start";
        this.nodes.foreach { (x) => { startCommand += ' '; startCommand ++= x } };
        this.producer.send(startCommand.toString);
      }
    } else {
      val lines: Array[String] = message.split('\n');
      lines.foreach { singleLine => {
        val fields: Array[String] = singleLine.split(' ');
        val id: Int = fields(0).toInt;
        val total: Double = fields(1).toDouble;
        val count: Int = fields(2).toInt;
        val cur = this.results.get(id).getOrElse((0.0, 0));
        this.results(id) = (cur._1 + total, cur._2 + count);
      }}

      this.updatesReceived += 1;
      if (this.updatesReceived == this.nodes.length) {
        // All nodes have sent us their replies
        this.results.foreach { x => println(s"${x._1}: ${x._2._1 / x._2._2}")};
      }
    }
  }
}
