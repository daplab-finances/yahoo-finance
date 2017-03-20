#!/bin/bash
set -x # for printing commands

mvn install -f pom.xml

# zookeeper-server-start.sh $KAFKA_HOME/config/zookeeper.properties
# kafka-server-start.sh $KAFKA_HOME/config/server.properties

nohup zookeeper-server-start.sh $KAFKA_HOME/config/zookeeper.properties &
nohup kafka-server-start.sh $KAFKA_HOME/config/server.properties &

java -cp target/finance-kafka-from-yahoo-0.0.1-SNAPSHOT-jar-with-dependencies.jar it.himyd.kafka.StockProducer localhost:9092 stock_topic 1000
