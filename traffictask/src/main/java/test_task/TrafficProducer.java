package test_task;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

public class TrafficProducer implements Runnable{

    private Properties properties = new Properties();
    private long delay = 300000;

    @Override
    public void run() {
        while(true) {
            getProperties();
            if (App.getTrafficSummary() > App.getMinLimit()) sendMessageToAlerts("Reached min limit with:" + App.getTrafficSummary());
            if (App.getTrafficSummary() > App.getMaxlimit()) sendMessageToAlerts("Reached max limit with:" + App.getTrafficSummary());
            App.setTrafficSummary(0);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void getProperties() {
        try {
            properties.put("bootstrap.servers", InetAddress.getLocalHost() + ":9092");
            properties.put("acks", "all");
            properties.put("linger.ms", 1);
            properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageToAlerts(String message){
        System.out.println(message);
        Producer<String, String> producer = new KafkaProducer<String, String>(properties);
        producer.send(new ProducerRecord<String, String>("alerts", message));
        producer.close();
    }
}
