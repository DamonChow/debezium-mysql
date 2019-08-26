package com.example.kakfa.json.parser;


import com.example.common.CharUtils;
import com.example.kakfa.json.model.ExtField;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 功能：
 *
 * @author Damon
 * @since 2019-04-22 11:06
 */
public class PointParser implements kafkaConsumerParser<Map<String, Object>, byte[]> {

    @Override
    public byte[] parse(ExtField fieldSchema, Map<String, Object> value) {
//        return null;
        List<byte[]> geo = fieldSchema.getFields().stream()
                .map(field -> {
                    Object o = value.get(field.getField());
                    if (o == null) {
                        return CharUtils.intToBytes(0);
                    } else if (o instanceof Double) {
                        return null;
                    }
                    return o.toString().getBytes();
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(array -> array.length))
                .collect(Collectors.toList());
//        return CharUtils.byteMergerAll(geo);
        return null;
    }
}
