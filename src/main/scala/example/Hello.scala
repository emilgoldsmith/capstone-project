package example

import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.RunInstancesRequest;

object Hello extends App {
  val client : AmazonEC2 = AmazonEC2ClientBuilder.defaultClient();

  client.createSecurityGroup(csgr);
}
