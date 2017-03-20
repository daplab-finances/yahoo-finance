#!/bin/bash
set -x # for printing commands

mvn install -f pom.xml

java -cp target/finance-kafka-from-yahoo-0.0.1-SNAPSHOT-jar-with-dependencies.jar it.himyd.kafka.StockProducer localhost:9092 stock_topic 1000
