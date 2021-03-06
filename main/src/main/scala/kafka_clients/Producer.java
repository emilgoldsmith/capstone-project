package kafka_clients;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * Created by sunilpatil on 12/28/15.
 */
public class Producer {
    private String hostAndPort;
    private String topicName;
    private org.apache.kafka.clients.producer.Producer kafkaProducer;

    public Producer(String hostAndPort, String topicName) throws Exception {
        this.hostAndPort = hostAndPort;
        this.topicName = topicName;
        //Configure the Producer
        Properties configProperties = new Properties();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,this.hostAndPort);
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.ByteArraySerializer");
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");

        this.kafkaProducer = new KafkaProducer(configProperties);
    }

    public void send(String message) {
      ProducerRecord<String, String> rec = new ProducerRecord<String, String>(topicName, message);
      this.kafkaProducer.send(rec);
    }

    public void stop() {
      this.kafkaProducer.close();
    }
}
