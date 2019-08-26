package com.example.kakfa.avro.parser;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.pulsar.shade.org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.util.Date;

/**
 * 功能：
 *
 * @author Damon
 * @since 2019-04-22 11:06
 */
@Slf4j
public class ZonedTimestampParser implements kafkaParser<Object, String> {

    @Override
    public String parse(Schema schema, Object input) {
        try {
            Date date = DateFormatUtils.ISO_DATETIME_FORMAT.parse(input.toString());
            return DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
        } catch (ParseException e) {
            log.error("解析ZonedTimestamp失败，", e);
        }
        return null;
    }
}
