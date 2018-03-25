package kafka_clients;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

import java.util.Arrays;
import java.util.Observer;
import java.util.Observable;
import java.util.Properties;
import java.util.Collection;

/**
 * inspired by creation of sunilpatil on 12/28/15.
 */
public class Consumer extends Observable {
    private ConsumerThread consumerRunnable;

    public Consumer(String hostAndPort, Collection<String> topicNames, String groupId) throws Exception {
        consumerRunnable = new ConsumerThread(hostAndPort, topicNames, groupId, this);
        consumerRunnable.start();
    }

    public void kill() throws Exception {
      consumerRunnable.getKafkaConsumer().wakeup();
      consumerRunnable.join();
    }

    public void broadcastMessage(String message) {
      this.setChanged();
      this.notifyObservers(message);
    }

    private static class ConsumerThread extends Thread {
        private String hostAndPort;
        private Collection<String> topicNames;
        private String groupId;
        private KafkaConsumer<String,String> kafkaConsumer;
        private Consumer host;

        public ConsumerThread(String hostAndPort, Collection<String> topicNames, String groupId, Consumer host){
            this.hostAndPort = hostAndPort;
            this.topicNames = topicNames;
            this.groupId = groupId;
            this.host = host;
        }

        public void run() {
            Properties configProperties = new Properties();
            configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, hostAndPort);
            configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArrayDeserializer");
            configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
            configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, "dummy");

            //Figure out where to start processing messages from
            kafkaConsumer = new KafkaConsumer<String, String>(configProperties);
            kafkaConsumer.subscribe(this.topicNames);
            // We only want to read messages that were sent to us after we started listening
            kafkaConsumer.seekToEnd(kafkaConsumer.assignment());
            //Start processing messages
            try {
                while (true) {
                    ConsumerRecords<String, String> records = kafkaConsumer.poll(100);
                    for (ConsumerRecord<String, String> record : records)
                        this.host.broadcastMessage(record.value());
                }
            }catch(WakeupException ex){
                System.out.println("Exception caught " + ex.getMessage());
            }finally{
                kafkaConsumer.close();
                System.out.println("After closing KafkaConsumer");
            }
        }
        public KafkaConsumer<String,String> getKafkaConsumer(){
           return this.kafkaConsumer;
        }
    }
}
