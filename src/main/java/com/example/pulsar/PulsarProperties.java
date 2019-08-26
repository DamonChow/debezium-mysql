package com.example.pulsar;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 功能：
 *
 * @author Damon
 * @since 2019-04-15 15:11
 */
@Data
@ConfigurationProperties(prefix = "pulsar")
public class PulsarProperties {

    private String serviceUrl;

    private PulsarConsumer consumer;

    @Data
    public static class PulsarConsumer {

        private String topic;

        private String subscriptionName;
    }
}
