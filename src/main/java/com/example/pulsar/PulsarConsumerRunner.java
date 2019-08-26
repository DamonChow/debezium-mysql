package com.example.pulsar;

import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;

import java.util.concurrent.Executors;

/**
 * 功能：
 *
 * @author Damon
 * @since 2019-04-15 15:05
 */
@Slf4j
@Order(1)
//@Component
public class PulsarConsumerRunner implements CommandLineRunner {

    @Autowired
    private Consumer consumer;

    @Override
    public void run(String... args) throws Exception {
        log.info("1------------------------------------");
        // Once the consumer is created, it can be used for the entire application lifecycle
        log.info("Created consumer for the topic {}", consumer.getTopic());

        Executors.newSingleThreadExecutor().execute(() -> {
            do {
                try {
                    // Wait until a message is available
                    Message<byte[]> msg = consumer.receive();

                    // Extract the message as a printable string and then log
                    String content = new String(msg.getData());
                    String content1 = new String(msg.getData());
                    String content2 = new String(msg.getData(), "GBK");
                    String content3 = new String(msg.getData(), "UTF-8");
                    String content6 = new String(msg.getData(), "UTF-16");
                    String content4 = new String(msg.getData(), "GB2312");
                    String content5 = new String(msg.getData(), "ISO-8859-1");
//            String[] playload = StringUtils.split(content, "\b");
//            String key = playload[0].substring(4, playload[0].length()-2);
//            String value = playload[1].substring(1, playload[1].length());
                    log.info("Received message '{}' with ID {}", content, msg.getMessageId());

//            new ObjectMapper().reader
                    // Acknowledge processing of the message so that it can be deleted
                    consumer.acknowledge(msg);
                } catch (Exception e) {
                    log.error("失败：：：：：", e);
                }
            } while (true);
        });

    }
}
