package com.example.kakfa.avro.sql;

import com.google.common.collect.Maps;
import io.debezium.data.Envelope;

import java.util.Map;

/**
 * 功能：
 *
 * @author Damon
 * @since 2019-04-22 15:52
 */
public class SqlProviderFactory {

    private static Map<Envelope.Operation, SqlProvider> map = Maps.newHashMap();

    static {
        map.put(Envelope.Operation.CREATE, new InsertSqlProvider());
        map.put(Envelope.Operation.UPDATE, new UpdateSqlProvider());
        map.put(Envelope.Operation.DELETE, new DeleteSqlProvider());
    }

    public static SqlProvider getProvider(Envelope.Operation operation) {
        return map.get(operation);
    }


}
