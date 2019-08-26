package com.example.embedded;

import io.debezium.data.Envelope;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;

import java.util.Objects;

/**
 * 功能：
 *
 * @author Damon
 * @since 2019-04-22 15:20
 */
@Slf4j
public class DebeziumRecordUtils {

    public static Struct getRecordStructValue(Struct payload, String sourceName) {
        Field source = payload.schema().schema().fields().stream()
                .filter(f -> Objects.equals(sourceName, f.name()))
                .findFirst().orElse(null);
        Object result = null;
        if (Objects.isNull(source)
                || Objects.isNull(result = payload.get(source))
                || !Struct.class.isInstance(result)) {
            return null;
        }
        return (Struct) result;
    }

    public static String getDDL(Struct payload) {
        return getStructStringProperties(payload,"ddl");
    }

    public static String getDatabaseName(Struct payload) {
        return getStructStringProperties(payload,"databaseName");
    }

    public static String getStructStringProperties(Struct payload, String properties) {
        try {
            return payload.getString(properties);
        } catch (Exception e) {
            log.error("not find {} field.", properties, e);
            return null;
        }
    }

    public static Envelope.Operation getOperation(Struct payload) {
        try {
            return Envelope.Operation.forCode(payload.getString("op"));
        } catch (Exception e) {
            log.error("not find op field.");
            return null;
        }
    }
}
