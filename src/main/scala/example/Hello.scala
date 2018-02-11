package example

import scala.io.Source;

import java.util.Collection;
import java.util.Vector;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.StartInstancesRequest;

object Hello {
  var ids : Array[String] = Array();
  val client : AmazonEC2 = AmazonEC2ClientBuilder.defaultClient();

  def startServers(numToStart : Int) : Unit = {
    val serversToStart : Array[String] = this.ids.take(numToStart)
    val serversToStartJavaCollection : Collection[String] = new Vector[String]();
    for (x <- serversToStart) {
      serversToStartJavaCollection.add(x);
    }
    val startRequest : StartInstancesRequest = new StartInstancesRequest()
      .withInstanceIds(serversToStartJavaCollection);
    this.client.startInstances(startRequest);
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

    if (args.length == 2 && args(0) == "start") {
      var numServersToStart : Int = -1;
      try {
        numServersToStart = args(1).toInt;
      } catch {
        case e: NumberFormatException => {
          println("invalid number argument to start command");
          System.exit(1);
        }
      }
      if (numServersToStart < 1 || numServersToStart > 10) {
        println("Start command error: Number of servers to start must be between 1 and 10");
        System.exit(1);
      }
      this.startServers(numServersToStart);
    } else {
      println("Usage: start <numServersToStart>")
      System.exit(1);
    }
  }
}
