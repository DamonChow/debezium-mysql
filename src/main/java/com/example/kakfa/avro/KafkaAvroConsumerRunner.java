package com.example.kakfa.avro;

import com.example.kakfa.avro.sql.SqlProvider;
import com.example.kakfa.avro.sql.SqlProviderFactory;
import io.debezium.data.Envelope;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericData;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * 功能：
 *
 * @author Damon
 * @since 2019-04-15 15:05
 */
@Slf4j
@Component
public class KafkaAvroConsumerRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedTemplate;

    @KafkaListener(id = "dbserver1-ddl-consumer", topics = "dbserver1")
    public void listenerUser(ConsumerRecord<GenericData.Record, GenericData.Record> record) throws Exception {
        GenericData.Record key = record.key();
        GenericData.Record value = record.value();
        log.info("Received record: {}", record);
        log.info("Received record: key {}", key);
        log.info("Received record: value {}", value);

        String databaseName = Optional.ofNullable(value.get("databaseName")).map(Object::toString).orElse(null);
        String ddl = Optional.ofNullable(value.get("ddl")).map(Object::toString).orElse(null);

        if (StringUtils.isBlank(ddl)) {
            return;
        }
        handleDDL(ddl, databaseName);
    }

    /**
     * 执行数据库ddl语句
     *
     * @param ddl
     */
    private void handleDDL(String ddl, String db) {
        log.info("ddl语句 : {}", ddl);
        try {
            if (StringUtils.isNotBlank(db)) {
                ddl = ddl.replace(db + ".", "");
                ddl = ddl.replace("`" + db + "`.", "");
            }

            jdbcTemplate.execute(ddl);
        } catch (Exception e) {
            log.error("数据库操作DDL语句失败，", e);
        }
    }

    @KafkaListener(id = "dbserver1-dml-consumer", topicPattern = "dbserver1.inventory.*")
    public void listenerAvro(ConsumerRecord<GenericData.Record, GenericData.Record> record) throws Exception {
        GenericData.Record key = record.key();
        GenericData.Record value = record.value();
        log.info("Received record: {}", record);
        log.info("Received record: key {}", key);
        log.info("Received record: value {}", value);

        if (Objects.isNull(value)) {
            return;
        }

        GenericData.Record source = (GenericData.Record) value.get("source");
        String table = source.get("table").toString();
        Envelope.Operation operation = Envelope.Operation.forCode(value.get("op").toString());

        String db = source.get("db").toString();

        handleDML(key, value, table, operation);
    }

    private void handleDML(GenericData.Record key, GenericData.Record value,
                           String table, Envelope.Operation operation) {
        SqlProvider provider = SqlProviderFactory.getProvider(operation);
        if (Objects.isNull(provider)) {
            log.error("没有找到sql处理器提供者.");
            return;
        }

        String sql = provider.getSql(key, value, table);
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

}
