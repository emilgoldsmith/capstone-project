package example

import scala.io.Source;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.StartInstancesRequest;

object Hello extends App {
  val client : AmazonEC2 = AmazonEC2ClientBuilder.defaultClient();

  // Read server ids
  val bufferedSource = Source.fromFile("resources/server_ids.json");
  val lines : List[String] = bufferedSource.getLines.toList;
  val partiallyCleanedIds : List[String] = lines.slice(1, lines.size - 1).map(x => x.trim);
  val ids : List[String] = partiallyCleanedIds.map(x => 
    if (x(x.length() - 1) == ',')
      x.substring(1, x.length() - 2)
    else
      // This is the case where it's the last element in the array
      x.substring(1, x.length() - 1)
  )
  for (x <- ids) {
    println(x)
  }

  

}
