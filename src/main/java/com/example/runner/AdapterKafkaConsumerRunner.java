package com.example.runner;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 功能：
 *
 * @author Damon
 * @since 2019-04-15 15:05
 */
@Slf4j
//@Component
public class AdapterKafkaConsumerRunner implements CommandLineRunner {

    @Autowired
    private Consumer kafkaConsumer;


    @Override
    public void run(String... args) throws Exception {
        while (true) {
            ConsumerRecords<Integer, String> records = kafkaConsumer.poll(100);
            records.forEach(record -> {
                log.info("Received record: key {}", record.key());
                log.info("Received record: value {}", record.value());
            });
            log.info("----------");

            // 提交最近的 offset
            kafkaConsumer.commitSync();
        }
    }
}
