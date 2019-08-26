package com.example.kakfa.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.Map;

@EnableKafka
@Configuration
@AutoConfigureAfter(KafkaAutoConfiguration.class)
public class KafkaConsumerConfiguration {

    private final KafkaProperties properties;

    public KafkaConsumerConfiguration(KafkaProperties properties) {
        this.properties = properties;
    }

//    @Bean
    public KafkaListenerContainerFactory kafkaListenerContainerFactory(ConsumerFactory<?,?> kafkaConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory();
        factory.setConsumerFactory(consumerFactory());
//        factory.getContainerProperties().setAckMode(MANUAL);
        return factory;
    }

//    @Bean
    public ConsumerFactory consumerFactory() {
        Map<String, Object> configs = properties.buildConsumerProperties();
        return new DefaultKafkaConsumerFactory(configs);
    }

    @Bean
    public ObjectMapper debeObjectMapper() {
        ObjectMapper map = new ObjectMapper();
        map.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        map.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return map;
    }

}
