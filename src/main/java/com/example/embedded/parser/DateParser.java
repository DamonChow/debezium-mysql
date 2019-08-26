package com.example.embedded.parser;


import org.apache.kafka.connect.data.Schema;

import java.time.LocalDate;

/**
 * 功能：
 *
 * @author Damon
 * @since 2019-04-22 11:06
 */
public class DateParser implements DebeziumParser<Object, LocalDate> {

    @Override
    public LocalDate parse(Schema schema, Object value) {
        return LocalDate.ofEpochDay((long) (int) value + 1);
    }
}
