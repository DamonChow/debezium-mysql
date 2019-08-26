package com.example.kakfa.json.parser;

import com.example.kakfa.json.model.ExtField;
import org.apache.pulsar.shade.org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

/**
 * 功能：
 *
 * @author Damon
 * @since 2019-04-22 11:06
 */
public class TimestampParser implements kafkaConsumerParser<Object, String> {

    @Override
    public String parse(ExtField field, Object value) {
        return DateFormatUtils.format(new Date((long) value), "yyyy-MM-dd HH:mm:ss");
    }
}
