<<<<<<< HEAD
# finance-yahoo-to-kafka

## Installation

### Zookeeper
[Download](http://zookeeper.apache.org/releases.html#download)

### Kafka
[Download](http://kafka.apache.org/documentation.html#quickstart_download)

## Startup Services
* `zookeeper-server-start.sh $KAFKA_HOME/config/zookeeper.properties`
* `kafka-server-start.sh $KAFKA_HOME/config/server.properties`

## Start Kafka JAR

### Build JAR with Maven
from `target/` folder:
* `mvn install -f ../pom.xml`

### Run JAR
* <code>`java -cp finance-kafka-from-yahoo-0.0.1-SNAPSHOT-jar-with-dependencies.jar it.daplab.kafka.StockProducer localhost:9092 stock_topic 1000`</code>
	* <code>`java -cp finance-kafka-from-yahoo-0.0.1-SNAPSHOT-jar-with-dependencies.jar <producer-class-file> <kafka-broker> <topics-seperated-by-comma> <request-delay-ms>`</code>

#### Parameters
* **producer-class-file** (not an args in java)
  * class file of the producer
* **kafka-broker**
  * give your kafka broker address with its port
* **topics-seperated-by-comma**
  * you can specify multiple kafka topics seperated by comma and implement your own logic for that topic
* **request-delay-ms**
  * Number of milliseconds the generator should wait before sending another request


=======
# yahoo-finance
>>>>>>> b76485fb16e596cf69b90c683d71df2f9a7b8483
