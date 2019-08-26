package com.example.kakfa.avro.parser;


import com.example.common.CharUtils;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;

import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 功能：
 *
 * @author Damon
 * @since 2019-04-22 11:06
 */
public class PointParser implements kafkaParser<GenericData.Record, byte[]> {

    @Override
    public byte[] parse(Schema schema, GenericData.Record value) {
        List<byte[]> list = value.getSchema().getFields().stream()
                .map(field -> {
                    Object o = value.get(field.name());
                    if (o == null) {
                        return CharUtils.intToBytes(0);
                    } else if (o instanceof Double) {
                        return null;
                    } else if (o instanceof ByteBuffer) {
                        return CharUtils.getByte((ByteBuffer) o);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(array -> array.length))
                .collect(Collectors.toList());
        return CharUtils.byteMergerAll(list);
    }
}
