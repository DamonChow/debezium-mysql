package com.example.embedded.parser;

import org.apache.kafka.connect.data.Schema;

/**
 * 功能：
 *
 * @author Damon
 * @since 2019-04-22 11:08
 */
@FunctionalInterface
public interface DebeziumParser<T, R> {

    R parse(Schema schema, T t);

}
