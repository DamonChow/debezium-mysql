package com.example.kakfa.avro.parser;


import org.apache.avro.Schema;

import java.time.LocalDate;

/**
 * 功能：
 *
 * @author Damon
 * @since 2019-04-22 11:06
 */
public class DateParser implements kafkaParser<Object, LocalDate> {

    @Override
    public LocalDate parse(Schema schema, Object value) {
        return LocalDate.ofEpochDay((long) (int) value + 1);
    }
}
