# TestTraffic

This application performs following functions:
- Reads limits values and dates
- Catches and processes traffic on local device
- Sends messages to Kafka topic 

To process traffic, the application uses the Pcap4j library, which requires administrator/root privileges and installed WinPCap/Libpcap libraries (windows/linux, respectively).
It is assumed that you have a PostgreSQL database that works with database "postgres" with access for the user "postges" with password "testpassword".

Sample database:

CREATE SCHEMA traffic_limits;
CREATE TABLE limits_per_hour(limit_name text, limit_value integer, effective_date timestamp);
INSERT INTO limits_per_hour(limit_name, limit_value, effective_date) VALUES ('min', 1024, '2019-12-17 04:05:06'),('max', 1073741824, '2019-12-17 04:05:06');


Kafka uses ZooKeeper so you need to first start a ZooKeeper server:

Linux: bin/zookeeper-server-start.sh config/zookeeper.properties 

Windows: bin\windows\zookeeper-server-start.bat config/zookeeper.properties

Then start the Kafka server:

Linux: bin/kafka-server-start.sh config/server.properties 

Windows: bin\windows\kafka-server-start.bat config/server.properties


Create a topic named "alerts" if you don't already have one:

Linux: bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic alerts 

Windows: bin\windows\kafka-topics.bat --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic alerts

Also you could run a command line consumer that will dump out messages to standard output:

Linux: bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic alerts --from-beginning

Windows: bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic alerts --from-beginning

Application is run by command:

java -jar traffictask.jar 

You can specify the ip-address as command-line argument, in this case only traffic for this address will be processed. For example:

java -jar traffictask.jar -192.168.0.1
