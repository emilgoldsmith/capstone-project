package group_by.simple;

import kafka_clients.Consumer;
import kafka_clients.Producer;
import kafka_clients.Admin;

import group_by.simple.Generator.generate;

import java.util.Observer;
import java.util.Observable;
import java.util.Collection;

import java.lang.Object;

import scala.collection.JavaConverters._;
import scala.collection.JavaConversions._;
import scala.collection.mutable.Map;

object Worker extends Observer {
  var hostAndPort: String = "";
  var groupId: String = "";
  var consumer: Consumer = null;
  var producer: Producer = null;
  var admin: Admin = null;
  var allWorkers: Array[String] = Array();
  var data: List[Map[String, Any]] = List[Map[String, Any]]();

  def main(args: Array[String]) {
    if (args.length != 2) {
      println("Usage: ./binary <host:port> <groupId>\n");
      System.exit(1);
    }

    this.hostAndPort = args(0);
    this.groupId = args(1);

    this.data = generate(100, 100);
    this.data.foreach { x => println(x.toString) };
    this.admin = new Admin(this.hostAndPort);
    this.admin.createTopics(List(this.groupId).asJava);
    this.consumer = new Consumer(this.hostAndPort, List("all-workers", this.groupId).asJava, this.groupId);
    this.consumer.addObserver(this);
    this.producer = new Producer(this.hostAndPort, "master");
    this.producer.send(s"connecting:${this.groupId}");
  }

  def update(observable: Observable, arg: Object) {
    val message: String = arg.asInstanceOf[String];
    val splitString: Array[String] = message.trim.split(" ");
    if (splitString.length > 0 && splitString(0) == "start") {
      splitString.slice(1, splitString.length).foreach { x => this.allWorkers = this.allWorkers :+ x };
      this.map();
    }
  }

  def map(): Unit = {
    var results: Map[Int, (Double, Int)] = Map[Int, (Double, Int)]();
    for (row <- this.data) {
      val stockId: Int = row.get("stockId").getOrElse(0).asInstanceOf[Int];
      val stockPrice: Double = row.get("stockPrice").getOrElse(0.0).asInstanceOf[Double];
      val cur = results.get(stockId).getOrElse((0.0, 0));
      results(stockId) = (cur._1 + stockPrice, cur._2 + 1);
    }
    var resultMessage: StringBuilder = new StringBuilder();
    results.foreach { x => {
      val id = x._1;
      val value = x._2;
      resultMessage ++= s"$id ${value._1} ${value._2}\n";
    }};
    this.producer.send(resultMessage.toString);
  }
}
