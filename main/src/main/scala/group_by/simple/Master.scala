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

import scala.util.matching.Regex;

import scala.io.Source;

import sys.process._

object Master extends Observer {
  var hostAndPort: String = "";
  var consumer: Consumer = null;
  var producer: Producer = null;
  var admin: Admin = null;
  var numNodes: Int = 0;
  var updatesReceived: Int = 0;
  var completesReceived: Int = 0;
  var nodes: List[String] = List();
  var results: Map[Int, (Double, Int)] = Map[Int, (Double, Int)]();
  var state = "connecting";
  val validCommands: Array[Regex] = Array("^group by\\s*$".r, "^generate \\d+ \\d+\\s*$".r, "^read [\\w\\.]+\\s*$".r, "^nodes \\d$".r);
  var startTime: Long = 0;
  var readingFromFile: Boolean = false;
  var fileLines: List[String] = List[String]();
  var fileLineIndex = 0;
  val workerIps = List("10.230.12.41", "10.230.12.42", "10.230.12.43", "10.230.12.44");
  val sshPort = "4410";

  def main(args: Array[String]) {
    if (args.length != 1) {
      println("Usage: ./binary <host:port>\n");
      System.exit(1);
    }

    this.hostAndPort = args(0);

    this.admin = new Admin(this.hostAndPort);
    this.admin.createTopics(List("master", "all-workers").asJava);
    this.consumer = new Consumer(this.hostAndPort, List("master").asJava, "master-group");
    this.consumer.addObserver(this);
    this.producer = new Producer(this.hostAndPort, "all-workers");
  }

  def update(observable: Observable, arg: Object) {
    val message: String = arg.asInstanceOf[String];
    // println(state, updatesReceived, completesReceived, message);
    if (state == "connecting") {
      val id = message.substring("connecting:".length());
      this.nodes = this.nodes :+ id;
      if (this.nodes.length == this.numNodes) {
        println("All workers connected:");
        this.nodes.foreach { println };
        this.state = "idle";
        this.runCLI();
      }
    } else if (this.state == "group-by") {
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
        val timeTaken = (System.nanoTime().toDouble - this.startTime.toDouble) / (1.0 * 1000 * 1000 * 1000);
        println("Group by results:");
        this.results.foreach { x => println(s"${x._1}: ${x._2._1 / x._2._2}")};
        println(s"It took ${timeTaken} seconds to run the query");
        this.state = "idle";
        this.updatesReceived = 0;
        this.results = Map[Int, (Double, Int)]();
        this.runCLI();
      }
    } else if (this.state == "generate") {
      if (message != "done") {
        println(s"Unexpected message that wasn't 'done' received: ${message}");
     }
      this.completesReceived += 1;
      println(message);
      if (this.completesReceived == this.nodes.length) {
        this.completesReceived = 0;
        this.state = "idle";
        this.runCLI();
      }
    } else {
      println(s"The following message was received at an unexpected time: \n${message}");
    }
  }

  def isValidCommand(command: String): Boolean = {
    for (regex <- this.validCommands) {
      val matches = !(regex findFirstIn command).isEmpty
      if (matches) {
        return true;
      }
    }
    return false;
  }

  def runCommand(command: String) {
    if (command.indexOf("nodes") == 0) {
      val splitString: Array[String] = command.trim.split(" ");
      this.workerIps.foreach { ip => {
        if (s"ssh $ip -p ${this.sshPort} ./capstone-project/deployment-resources/kill-local-workers.sh".! != 0) {
          println(s"Error killing workers on ip $ip");
        }
      }}
      this.nodes = List();
      this.numNodes = splitString(1).toInt;
      this.state = "connecting";
      for ( i <- 0 until this.numNodes ) {
        val ip = this.workerIps(i);
        if (s"ssh $ip -p ${this.sshPort} setsid ./capstone-project/worker 10.230.12.40:9092 worker${i}".! != 0) {
          println(s"Error starting worker on ip $ip");
        } else {
          println(s"worker $ip started successfully");
        }
      }
      println("Waiting for workers to connect");
    }
    if (command == "group by") {
      this.state = "group-by";
      var startCommand: StringBuilder = new StringBuilder;
      startCommand ++= "start";
      this.nodes.foreach { (x) => { startCommand += ' '; startCommand ++= x } };
      this.producer.send(startCommand.toString);
      this.startTime = System.nanoTime();
    } else if (command.indexOf("generate ") == 0) {
      val splitString: Array[String] = command.trim.split(" ");
      if (splitString.length != 3) {
        println("Incorrect usage, correct usage: generate <numberOfSeparateDays> <rowsPerDay>")
      }
      val numDays = splitString(1).toInt;
      val rowsPerDay = splitString(2).toInt;
      println(s"Setting test data to $numDays days with $rowsPerDay rows for each day");
      this.producer.send(s"generate ${numDays} ${rowsPerDay}");
      this.state = "generate";
    } else if (command.indexOf("read ") == 0) {
      val splitString: Array[String] = command.trim.split(" ");
      if (splitString.length != 2) {
        println("Incorrect usage, correct usage: read path/to/file");
      }
      readingFromFile = true;
      fileLineIndex = 0;
      fileLines = List[String]();
      val source = Source.fromFile(splitString(1));
      for (line <- source.getLines) {
        fileLines = fileLines :+ line;
      }
      source.close;
      this.runCLI();
    } else {
      println("I don't understand that command");
    }
  }

  def runCLI() {
    if (!readingFromFile) {
      var command = readLine("aquery >> ");
      while (!isValidCommand(command)) {
        println("I don't understand that command");
        println(command);
        command = readLine("aquery >> ");
        if (command == null) {
          println ("\nGoodbye!");
          System.exit(0);
        }
      }
      runCommand(command);
    } else {
      var command = fileLines(fileLineIndex);
      if (!isValidCommand(command)) {
        println("File contained invalid command:");
        println(command);
        readingFromFile = false;
      } else {
        fileLineIndex += 1;
        if (fileLineIndex == fileLines.length) {
          readingFromFile = false;
        }
        runCommand(command);
      }
    }

  }
}
