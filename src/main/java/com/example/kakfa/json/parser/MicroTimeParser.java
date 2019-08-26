package com.example.kakfa.json.parser;

import com.example.kakfa.json.model.ExtField;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

/**
 * 功能：
 *
 * @author Damon
 * @since 2019-04-22 11:06
 */
public class MicroTimeParser implements kafkaConsumerParser<Object, LocalTime> {


    @Override
    public LocalTime parse(ExtField field, Object value) {
        return LocalTime.ofNanoOfDay((long) value * TimeUnit.MICROSECONDS.toNanos(1));
    }
}
