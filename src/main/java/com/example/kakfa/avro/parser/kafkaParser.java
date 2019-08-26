package com.example.kakfa.avro.parser;

import org.apache.avro.Schema;

/**
 * 功能：
 *
 * @author Damon
 * @since 2019-04-22 11:08
 */
@FunctionalInterface
public interface kafkaParser<T, R> {

    R parse(Schema schema, T t);

}
