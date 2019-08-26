package com.example.embedded.parser;


import com.example.common.CharUtils;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 功能：
 *
 * @author Damon
 * @since 2019-04-22 11:06
 */
public class GeometryParser implements DebeziumParser<Object, byte[]> {

    @Override
    public byte[] parse(Schema schema, Object value) {
        List<byte[]> geo = schema.fields().stream()
                .map(field -> {
                    Object o = ((Struct) value).get(field);
                    if (o == null) {
                        return CharUtils.intToBytes(0);
                    }
                    return (byte[]) o;
                })
                .sorted(Comparator.comparing(array -> array.length))
                .collect(Collectors.toList());
        return CharUtils.byteMergerAll(geo);
    }
}
