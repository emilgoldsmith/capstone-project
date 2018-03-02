package kafka_clients;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Properties;
import java.util.Collection;
import java.util.Vector;

/**
 * Created by sunilpatil on 12/28/15.
 */
public class Admin {
    private String hostAndPort;
    private AdminClient kafkaAdmin;

    public Admin(String hostAndPort) {
        this.hostAndPort = hostAndPort;

        //Configure the Admin
        Properties configProperties = new Properties();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,this.hostAndPort);
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.ByteArraySerializer");
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");

        this.kafkaAdmin = AdminClient.create(configProperties);
    }

    public void createTopics(Collection<String> newTopicNames) {
      Collection<NewTopic> newTopics = new Vector<NewTopic>();
      for (String topicName : newTopicNames) {
        newTopics.add(new NewTopic(topicName, (short)1, (short)1));
      }
      this.kafkaAdmin.createTopics(newTopics);
    }

    public void stop() {
      this.kafkaAdmin.close();
    }
}
