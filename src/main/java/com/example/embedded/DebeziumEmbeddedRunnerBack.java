/*
package com.example.embedded;

import avro.shaded.com.google.common.collect.Lists;
import io.debezium.data.Envelope;
import io.debezium.embedded.EmbeddedEngine;
import io.debezium.util.Clock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.connect.data.ExtField;
import org.apache.kafka.connect.data.ExtSchema;
import org.apache.kafka.connect.data.KeyStruct;
import org.apache.kafka.connect.json.JsonConverter;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

*/
/**
 * 功能：
 *
 * @author Damon
 * @since 2019-04-15 15:05
 *//*

@Slf4j
//@Order(2)
//@Component
public class DebeziumEmbeddedRunnerBack implements CommandLineRunner {

    @Autowired
    private io.debezium.config.Configuration embeddedConfig;

    @Autowired
    private JsonConverter keyConverter;

    @Autowired
    private JsonConverter valueConverter;

    @Autowired
    private Properties embeddedProperties;

    @Autowired
    private PulsarClient client;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedTemplate;

    private final Map<String, Producer<String>> producers = new ConcurrentHashMap<>();

    @Override
    public void run(String... args) throws Exception {
        EmbeddedEngine engine = EmbeddedEngine.create()
                .using(embeddedConfig)
                .using(this.getClass().getClassLoader())
                .using(Clock.SYSTEM)
                .notifying(this::sendRecord)
                .build();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(engine);

        shutdownHook(engine);

        awaitTermination(executor);
    }

    private void shutdownHook(EmbeddedEngine engine) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Requesting embedded engine to shut down");
            engine.stop();
        }));
    }

    */
/**
     * For every record this method will be invoked.
     *//*

    private void sendRecord(SourceRecord record) {
        KeyStruct payload = (KeyStruct) record.value();
        if (Objects.isNull(payload)) {
            return;
        }

        ExtField source = payload.schema().schema().fields().stream().filter(f -> Objects.equals("source", f.name())).findFirst().orElse(null);
        KeyStruct sourceValue = (KeyStruct) payload.get(source);
        String db = sourceValue.getString("db");
        String table = sourceValue.getString("table");

        log.info("db is {}, table is {}", db, table);

        // 处理数据DML
        List<String> idList = Lists.newArrayList();
        List<String> primaryKeyList = Lists.newArrayList();
        Envelope.Operation operation = getOperation(payload);
        if (Objects.nonNull(operation)) {
            KeyStruct key = (KeyStruct) record.key();
            key.schema().fields().stream().forEach(f -> {
                ExtSchema.Type type = f.schema().type();
                String name = f.name();
                int index = f.index();
                Object id = key.get(f);
                log.info("name is {}, index is {}, id is {}", name, index, id);
                if (Objects.equals(ExtSchema.Type.STRING, type)) {
                    idList.add(name + "='" + id + "'");
                } else {
                    idList.add(name + "=" + id);
                }
                primaryKeyList.add(name);
            });
            List<String> valueList = Lists.newArrayList();
            List<String> fieldList = Lists.newArrayList();
            String sql = null;
            ExtField after = payload.schema().schema().fields().stream().filter(f -> Objects.equals("after", f.name())).findFirst().orElse(null);
            KeyStruct afterValue = (KeyStruct) payload.get(after);

            switch (operation) {
                case CREATE:
                    afterValue.schema().fields().stream()
                            .forEach(f -> {
                                ExtSchema.Type type = f.schema().type();
                                String field = f.name();
                                fieldList.add(field);

                                Object value = afterValue.get(f);
                                if (Objects.equals(ExtSchema.Type.STRING, type)) {
                                    valueList.add(Optional.ofNullable(value).map(s -> "'" + s + "'").orElse("null"));
                                } else {
                                    valueList.add(Optional.ofNullable(value).map(Object::toString).orElse("null"));
                                }
                            });

                    sql = "insert into  " + db + "." + table
                            + " (" + StringUtils.join(fieldList, ",") + ") "
                            + " values "
                            + "(" + StringUtils.join(valueList, ", ") + ")";
                    break;
                case UPDATE:
                    afterValue.schema().fields().stream()
                            .filter(f -> !primaryKeyList.contains(f.name()))
                            .forEach(f -> {
                                ExtSchema.Type type = f.schema().type();
                                String field = f.name();

                                Object value = afterValue.get(f);
                                if (Objects.equals(ExtSchema.Type.STRING, type)) {
                                    valueList.add(field + "='" + value + "'");
                                } else {
                                    valueList.add(field + "=" + value);
                                }
                            });


                    sql = "update " + db + "." + table
                            + " set " + StringUtils.join(valueList, ",")
                            + " where " + StringUtils.join(idList, " and ");
                    break;
                case DELETE:
                    sql = "delete from " + db + "." + table
                            + " where " + StringUtils.join(idList, " and ");

                    break;
                default:
                    break;
            }
            log.info("sql = {}", sql);
//            return;
        }

        // 处理结构DDL
        String ddl = getDDL(payload);
        if (StringUtils.isNotBlank(ddl)) {
            log.info("打印ddl语句：{}", ddl);
        }
        send(record);
    }

    private String getDDL(KeyStruct payload) {
        try {
            return payload.getString("ddl");
        } catch (Exception e) {
            log.error("not find ddl field.");
            return null;
        }
    }

    private Envelope.Operation getOperation(KeyStruct payload) {
        try {
            return Envelope.Operation.forCode(payload.getString("op"));
        } catch (Exception e) {
            log.error("not find op field.");
            return null;
        }
    }

    private void send(SourceRecord record) {
        final byte[] payload = valueConverter.fromConnectData("dummy", record.valueSchema(), record.value());
        final byte[] key = keyConverter.fromConnectData("dummy", record.keySchema(), record.key());
        log.info("1Publishing Topic --> {}", record.topic());
        log.info("1Key --> {}", new String(key));
        log.info("1Payload --> {}", new String(payload));

        */
/*Producer<String> producer = getProducer(record.topic());
        while (true) {
            try {
                MessageId msgID = producer.newMessage()
                        .key(new String(key))
                        .value(new String(payload))
                        .send();

                log.debug("Published message with id {}", msgID);
                break;
            }
            catch (PulsarClientException e) {
                throw new RuntimeException("Couldn't send message", e);
            }
        }*//*

    }

    private Producer<String> getProducer(String topic) {
        String topicFormat = embeddedProperties.getProperty("pulsar.topic");
        String topicURI = MessageFormat.format(topicFormat, topic);

        Producer<String> producer = producers.computeIfAbsent(topic, t -> {
            try {
                return client.newProducer(org.apache.pulsar.client.api.ExtSchema.STRING)
                        .topic(topicURI)
                        .create();
            } catch (PulsarClientException e) {
                throw new RuntimeException(e);
            }
        });

        return producer;
    }

    private void awaitTermination(ExecutorService executor) {
        try {
            while (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                log.info("Waiting another 10 seconds for the embedded engine to shut down");
            }
        } catch (InterruptedException e) {
            Thread.interrupted();
        }
    }
}
*/
