package com.example.kakfa.avro.parser;

import org.apache.avro.Schema;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

/**
 * 功能：
 *
 * @author Damon
 * @since 2019-04-22 11:06
 */
public class MicroTimeParser implements kafkaParser<Object, LocalTime> {


    @Override
    public LocalTime parse(Schema schema, Object value) {
        return LocalTime.ofNanoOfDay((long) value * TimeUnit.MICROSECONDS.toNanos(1));
    }
}
