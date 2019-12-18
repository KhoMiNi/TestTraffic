package test_task;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

public class TrafficProducer implements Runnable{
    @Override
    public void run() {
        while(true) {
            Properties properties = new Properties();
            try {
                properties.put("bootstrap.servers", InetAddress.getLocalHost() + ":9092");
                //properties.put("bootstrap.servers", "127.0.0.1:9092");
                properties.put("acks", "all");
                properties.put("linger.ms", 1);
                properties.put("key.deserializer",
                        "org.apache.kafka.common.serialization.StringDeserializer");
                properties.put("value.deserializer",
                        "org.apache.kafka.common.serialization.StringDeserializer");
                properties.put("key.serializer",
                        "org.apache.kafka.common.serialization.StringSerializer");
                properties.put("value.serializer",
                        "org.apache.kafka.common.serialization.StringSerializer");
                System.out.println("Checking limits");
                if (App.getTrafficSummary() > App.getMinLimit()) {
                    System.out.println("min limit" + App.getTrafficSummary());
                    Producer<String, String> producer1 = new KafkaProducer<String, String>(properties);

                    producer1.send(new ProducerRecord<String, String>("alerts", "Reached min limit with:" + App.getTrafficSummary()));
                    producer1.close();

                }
                if (App.getTrafficSummary() > App.getMaxlimit()) {
                    System.out.println("max limit" + App.getTrafficSummary());
                    Producer<String, String> producer2 = new KafkaProducer<String, String>(properties);
                    producer2.send(new ProducerRecord<String, String>("alerts", "Reached max limit with:" + App.getTrafficSummary()));
                    producer2.close();
                }

            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            App.setTrafficSummary(0);
            try {
                Thread.sleep(300000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
