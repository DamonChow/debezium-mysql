package com.example.pulsar;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.Properties;

/**
 * 功能：
 *
 * @author Damon
 * @since 2019-04-15 15:11
 */
//@Configuration
//@EnableConfigurationProperties(PulsarProperties.class)
public class PulsarAutoConfiguration {

    @Autowired
    private PulsarProperties pulsarProperties;

    @Bean
    public PulsarClient client() throws PulsarClientException {
        return PulsarClient.builder()
                .serviceUrl(pulsarProperties.getServiceUrl())
                .build();
    }

    @Bean
    public Consumer consumer() throws PulsarClientException {
        return client().newConsumer()
                .topic(pulsarProperties.getConsumer().getTopic())
                .subscriptionName(pulsarProperties.getConsumer().getSubscriptionName())
//                .ackTimeout(10, TimeUnit.SECONDS)
//                .subscriptionType(SubscriptionType.Exclusive)
                .subscribe();
    }

    @Bean
    public org.apache.kafka.clients.consumer.Consumer kafkaConsumer() {
        Properties props = new Properties();
        // 指向一个Pulsar服务
        props.put("bootstrap.servers", pulsarProperties.getServiceUrl());
        props.put("group.id", pulsarProperties.getConsumer().getSubscriptionName());
        props.put("enable.auto.commit", "false");
        props.put("key.deserializer", IntegerDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());

        org.apache.kafka.clients.consumer.Consumer<Integer, String> consumer = new KafkaConsumer(props);
        consumer.subscribe(Arrays.asList(pulsarProperties.getConsumer().getTopic()));

        return consumer;
    }
}
