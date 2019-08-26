package com.example.kakfa.avro;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Collections;
import java.util.Properties;

/**
 * 功能：
 *
 * @author Damon
 * @since 2019-04-24 15:55
 */
@Slf4j
public class ConfluentConsumer {

    public static void main(String[] args) throws Exception {

        Properties props = new Properties();
        props.put("bootstrap.servers", "127.0.0.1:9092");
        props.put("group.id", "dev3-yangyunhe-group001");
        props.put("key.deserializer", "io.confluent.kafka.serializers.KafkaAvroDeserializer");
        // 使用Confluent实现的KafkaAvroDeserializer
        props.put("value.deserializer", "io.confluent.kafka.serializers.KafkaAvroDeserializer");
        // 添加schema服务的地址，用于获取schema
        props.put("schema.registry.url", "http://127.0.0.1:8081");
        KafkaConsumer consumer = new KafkaConsumer(props);

        consumer.subscribe(Collections.singletonList("dbserver1.inventory.demo"));

        try {
            while (true) {
                ConsumerRecords records = consumer.poll(1000);
                for (Object record : records) {
                    log.info("record is {}", record);
//                    log.info("key is {}", record.key());
//                    log.info("value is {}", record.value());
                }
            }
        } finally {
            consumer.close();
        }
    }
}
