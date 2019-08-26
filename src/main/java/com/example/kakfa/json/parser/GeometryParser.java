package com.example.kakfa.json.parser;


import com.example.kakfa.json.model.ExtField;

import java.util.Map;

/**
 * 功能：
 *
 * @author Damon
 * @since 2019-04-22 11:06
 */
public class GeometryParser implements kafkaConsumerParser<Map<String, Object>, byte[]> {

    @Override
    public byte[] parse(ExtField fieldSchema, Map<String, Object> value) {
//        List<byte[]> geo = fieldSchema.getFields().stream()
//                .map(field -> {
//                    Object o = value.get(field.getField());
//                    if (o == null) {
//                        return CharUtils.intToBytes(0);
//                    }
//                    return o.toString().getBytes();
//                })
//                .sorted(Comparator.comparing(array -> array.length))
//                .collect(Collectors.toList());
//        return CharUtils.byteMergerAll(geo);
        return null;
    }
}
