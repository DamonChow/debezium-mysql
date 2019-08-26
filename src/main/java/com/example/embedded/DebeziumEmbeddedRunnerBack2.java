/*
package com.example.embedded.sql;

import com.example.embedded.DebeziumRecordUtils;
import io.debezium.connector.mysql.MySqlConnectorConfig;
import io.debezium.data.Envelope;
import io.debezium.embedded.EmbeddedEngine;
import io.debezium.util.Clock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
@Order(2)
@Component
public class DebeziumEmbeddedRunnerBack2 implements CommandLineRunner {

    @Autowired
    private io.debezium.config.Configuration embeddedConfig;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedTemplate;

    @Autowired
    private JsonConverter keyConverter;

    @Autowired
    private JsonConverter valueConverter;

    @Autowired
    private Properties embeddedProperties;

    //    @Autowired
    private PulsarClient client;

    private final Map<String, Producer<String>> producers = new ConcurrentHashMap<>();

    @Override
    public void run(String... args) throws Exception {
        EmbeddedEngine engine = EmbeddedEngine.create()
                .using(embeddedConfig)
                .using(this.getClass().getClassLoader())
                .using(Clock.SYSTEM)
                .notifying(this::handleRecord)
                .build();

        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(engine);

        shutdownHook(engine);

        awaitTermination(executor);
    }

    */
/**
     * For every record this method will be invoked.
     *//*

    private void handleRecord(SourceRecord record) {
        sendRecord2MQ(record);

        KeyStruct payload = (KeyStruct) record.value();
        if (Objects.isNull(payload)) {
            return;
        }
        String table = Optional.ofNullable(DebeziumRecordUtils.getRecordStructValue(payload, "source"))
                .map(s->s.getString("table")).orElse(null);

        // 处理数据DML
        Envelope.Operation operation = DebeziumRecordUtils.getOperation(payload);
        if (Objects.nonNull(operation)) {
            KeyStruct key = (KeyStruct) record.key();
            handleDML(key, payload, table, operation);
            return;
        }

        // 处理结构DDL
        String ddl = DebeziumRecordUtils.getDDL(payload);
        if (StringUtils.isNotBlank(ddl)) {
            handleDDL(ddl);
        }
    }

    */
/**
     * 执行数据库ddl语句
     *
     * @param ddl
     *//*

    private void handleDDL(String ddl) {
        try {
            String db = embeddedConfig.getString(MySqlConnectorConfig.DATABASE_WHITELIST);
            ddl = ddl.replace(db + ".", "");
            ddl = ddl.replace("`" + db + "`.", "");
            log.info("ddl语句 : {}", ddl);
            jdbcTemplate.execute(ddl);
        } catch (Exception e) {
            log.error("数据库操作DDL语句失败，", e);
        }
    }

    */
/**
     * 处理insert,update,delete等DML语句
     *
     * @param key       表主键修改事件结构
     * @param payload   表正文响应
     * @param table     表名
     * @param operation DML操作类型
     *//*

    private void handleDML(KeyStruct key, KeyStruct payload, String table, Envelope.Operation operation) {
        SqlProvider provider = SqlProviderFactory.getProvider(operation);
        if (Objects.isNull(provider)) {
            log.error("没有找到sql处理器提供者.");
            return;
        }

        String sql = provider.getSql(key, payload, table);
        if (StringUtils.isBlank(sql)) {
            log.error("找不到sql.");
            return;
        }

        try {
            log.info("dml语句 : {}", sql);
            namedTemplate.update(sql, provider.getSqlParameterMap());
        } catch (Exception e) {
            log.error("数据库DML操作失败，", e);
        }
    }

    */
/**
     * 发送MQ消息
     *
     * @param record
     *//*

    private void sendRecord2MQ(SourceRecord record) {
        final byte[] payload = valueConverter.fromConnectData("dummy", record.valueSchema(), record.value());
        final byte[] key = keyConverter.fromConnectData("dummy", record.keySchema(), record.key());
        log.info("Publishing Topic --> {}", record.topic());
        log.info("Key --> {}", new String(key));
        log.info("Payload --> {}", new String(payload));

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

    private void shutdownHook(EmbeddedEngine engine) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Requesting embedded engine to shut down");
            engine.stop();
        }));
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


//    public static void main(String[] args) throws ParseException {
//        String dateString = "2019-04-16T16:01:04Z";
//        Date date = ISO_8601_EXTENDED_DATETIME_FORMAT.parse(dateString);
//        log.info("date is {}", date);
//        String format = org.apache.pulsar.shade.org.apache.commons.lang3.time.DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
//        log.info("format is {}", format);
//        java.sql.Timestamp timestamp = new Timestamp(date.getTime());
//        log.info("timestamp is {}", timestamp);
//
//        format = DateFormatUtils.format(new Date(1554140195000L), "yyyy-MM-dd HH:mm:ss");
//        log.info("format is {}", format);
//
//        long nan = TimeUnit.MILLISECONDS.toNanos(1);
//        log.info("nan is {}", nan);
//        LocalTime time = LocalTime.now();
//        long micros = Math.floorDiv(time.toNanoOfDay(), nan);
//        log.info("time is {}", time);
//        log.info("micros is {}", micros);
//
////        time is 18:03:59.616
////        micros is 65039616
//
//        long hours = TimeUnit.MILLISECONDS.toHours(65039616L);
//        log.info("hours is {}", hours);
//        long mim = 65039616L - TimeUnit.HOURS.toMillis(hours);
//        log.info("mim is {}", mim);
//        long minutes = TimeUnit.MILLISECONDS.toMinutes(mim);
//        log.info("minutes is {}", minutes);
//        long se = mim - TimeUnit.MINUTES.toMillis(minutes);
//        log.info("se is {}", se);
//        long second = TimeUnit.MILLISECONDS.toSeconds(se);
//        log.info("second is {}", second);
//
//        LocalTime localTime = LocalTime.ofNanoOfDay(65039616L * nan);
//        log.info("localTime is {}", localTime);
//
//
//        Instant instant = Conversions.toInstantFromMicros(1554140195000L);
//        log.info("instant is {}", instant);
//        instant = Conversions.toInstant(1554140195000L);
//        log.info("instant is {}", instant);
//        log.info("instant is {}", Instant.now());
////        instant.
//
//
//        int a = 12345678;
//        ByteBuffer buf = ByteBuffer.wrap(intToBytes(a));
//        buf.order(ByteOrder.LITTLE_ENDIAN);
//
//        // first 4 bytes are SRID
//        Integer srid = buf.getInt();
//        log.info("srid is {}", srid);
//    }

}*/
