package example

import scala.io.Source;
import scala.collection.JavaConversions._

import java.util.Collection;
import java.util.Vector;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.StopInstancesRequest;


object Hello {
  var ids : Array[String] = Array();
  val client : AmazonEC2 = AmazonEC2ClientBuilder.defaultClient();

  def startServers(startIndex : Int, endIndex : Int) : Unit = {
    val serversToStart : Array[String] = this.ids.slice(startIndex, endIndex)
    val serversToStartJavaCollection : Collection[String] = new Vector[String]();
    for (x <- serversToStart) {
      serversToStartJavaCollection.add(x);
    }
    val startRequest : StartInstancesRequest = new StartInstancesRequest()
      .withInstanceIds(serversToStartJavaCollection);
    this.client.startInstances(startRequest);
    println("Servers successfully started");
  }

  def getRunningInstanceIds() : Vector[String] = {
    var runningInstanceIds : Vector[String] = new Vector[String]();
    val response : DescribeInstancesResult = this.client.describeInstances(new DescribeInstancesRequest);
    for (reservation : Reservation <- response.getReservations()) {
      for (instance : Instance <- reservation.getInstances()) {
        if (instance.getState().getName() == "running") {
          runningInstanceIds.add(instance.getInstanceId());
        }
      }
    }
    return runningInstanceIds;
  }

  def stopServers() : Unit = {
    val instanceIds : Vector[String] = this.getRunningInstanceIds();
    if (instanceIds.length == 0) {
      println("No instances currently running so no instances stopped");
      return;
    }
    val request : StopInstancesRequest = new StopInstancesRequest()
      .withInstanceIds(instanceIds);
    this.client.stopInstances(request);
    println("Servers successfully stopped");
  }

  def main(args: Array[String]) {
    // Read server ids
    val bufferedSource = Source.fromFile("resources/server_ids.json");
    val lines : Array[String] = bufferedSource.getLines.toArray;
    val partiallyCleanedIds : Array[String] = lines.slice(1, lines.size - 1).map(x => x.trim);
    this.ids = partiallyCleanedIds.map(x =>
      if (x(x.length() - 1) == ',')
        x.substring(1, x.length() - 2)
      else
        // This is the case where it's the last element in the array
        x.substring(1, x.length() - 1)
    )

    if ((args.length == 2 || args.length == 3) && args(0) == "start") {
      var startIndex : Int = -1;
      var endIndex : Int = -1;
      try {
        if (args.length == 2) {
          endIndex = args(1).toInt;
          startIndex = 1;
        } else {
          // it must be 3 because of top level conditional
          startIndex = args(1).toInt;
          endIndex = args(2).toInt;
        }
      } catch {
        case e: NumberFormatException => {
          println("invalid integer argument to start command");
          System.exit(1);
        }
      }
      if (startIndex < 1 || startIndex > 10 ||
          endIndex < 1 || endIndex > 10) {
        println("Start command error: integer arguments must be between 1 and 10");
        System.exit(1);
      }
      this.startServers(startIndex - 1, endIndex);
    } else if (args.length == 1 && args(0) == "stop") {
      this.stopServers();
    } else {
      println("Usage:\nstart <numServersToStart>\nOR\nstart <startIndex> <endIndex>\nOR\nstop")
      System.exit(1);
    }
  }
}
