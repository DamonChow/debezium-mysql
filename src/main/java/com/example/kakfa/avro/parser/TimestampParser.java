package com.example.kakfa.avro.parser;

import org.apache.avro.Schema;
import org.apache.pulsar.shade.org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

/**
 * 功能：
 *
 * @author Damon
 * @since 2019-04-22 11:06
 */
public class TimestampParser implements kafkaParser<Object, String> {

    @Override
    public String parse(Schema schema, Object value) {
        return DateFormatUtils.format(new Date((long) value), "yyyy-MM-dd HH:mm:ss");
    }
}
