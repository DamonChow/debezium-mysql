package com.example.embedded;

import com.example.embedded.sql.AbstractDebeziumSqlProvider;
import com.example.embedded.sql.DebeziumSqlProviderFactory;
import io.debezium.connector.mysql.MySqlConnectorConfig;
import io.debezium.data.Envelope;
import io.debezium.embedded.EmbeddedEngine;
import io.debezium.util.Clock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.json.JsonConverter;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 功能：
 *
 * @author Damon
 * @since 2019-04-15 15:05
 */
@Slf4j
@Order(2)
//@Component
public class DebeziumEmbeddedRunner implements CommandLineRunner {

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

        log.info("begin---------------");
    }

    /**
     * For every record this method will be invoked.
     */
    private void handleRecord(SourceRecord record) {
        logRecord(record);

        Struct payload = (Struct) record.value();
        if (Objects.isNull(payload)) {
            return;
        }
        String table = Optional.ofNullable(DebeziumRecordUtils.getRecordStructValue(payload, "source"))
                .map(s->s.getString("table")).orElse(null);

//        // 处理数据DML
        Envelope.Operation operation = DebeziumRecordUtils.getOperation(payload);
        if (Objects.nonNull(operation)) {
            Struct key = (Struct) record.key();
            handleDML(key, payload, table, operation);
            return;
        }
//
//        // 处理结构DDL
        String ddl = getDDL(payload);
        if (StringUtils.isNotBlank(ddl)) {
            handleDDL(ddl);
        }
    }

    private String getDDL(Struct payload) {
        String ddl = DebeziumRecordUtils.getDDL(payload);
        if (StringUtils.isBlank(ddl)) {
            return null;
        }
        String db = DebeziumRecordUtils.getDatabaseName(payload);
        if (StringUtils.isBlank(db)) {
            db = embeddedConfig.getString(MySqlConnectorConfig.DATABASE_WHITELIST);
        }
        ddl = ddl.replace(db + ".", "");
        ddl = ddl.replace("`" + db + "`.", "");
        return ddl;
    }

    /**
     * 执行数据库ddl语句
     *
     * @param ddl
     */
    private void handleDDL(String ddl) {
        log.info("ddl语句 : {}", ddl);
        try {
            jdbcTemplate.execute(ddl);
        } catch (Exception e) {
            log.error("数据库操作DDL语句失败，", e);
        }
    }

    /**
     * 处理insert,update,delete等DML语句
     *
     * @param key       表主键修改事件结构
     * @param payload   表正文响应
     * @param table     表名
     * @param operation DML操作类型
     */
    private void handleDML(Struct key, Struct payload, String table, Envelope.Operation operation) {
        AbstractDebeziumSqlProvider provider = DebeziumSqlProviderFactory.getProvider(operation);
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

    /**
     * 打印消息
     *
     * @param record
     */
    private void logRecord(SourceRecord record) {
        final byte[] payload = valueConverter.fromConnectData("dummy", record.valueSchema(), record.value());
        final byte[] key = keyConverter.fromConnectData("dummy", record.keySchema(), record.key());
        log.info("Publishing Topic --> {}", record.topic());
        log.info("Key --> {}", new String(key));
        log.info("Payload --> {}", new String(payload));
    }

    private void shutdownHook(EmbeddedEngine engine) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Requesting embedded engine to shut down");
            engine.stop();
        }));
    }

    private void awaitTermination(ExecutorService executor) {
        try {
            while (!executor.awaitTermination(10L, TimeUnit.SECONDS)) {
                log.info("Waiting another 10 seconds for the embedded engine to shut down");
            }
        } catch (InterruptedException e) {
            Thread.interrupted();
        }
    }

}