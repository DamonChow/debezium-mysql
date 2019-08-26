package com.example.embedded.sql;

import avro.shaded.com.google.common.collect.Lists;
import com.example.common.CharUtils;
import com.example.embedded.DebeziumRecordUtils;
import com.example.embedded.parser.DebeziumParser;
import com.example.embedded.parser.ParserFactory;
import com.google.common.collect.Maps;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 功能：
 *
 * @author Damon
 * @since 2019-04-22 15:15
 */
public abstract class AbstractDebeziumSqlProvider {

    /**
     * 主键值作为参数的map
     */
    private Map<String, Object> sqlParameterMap = null;

    /**
     * 表列预编译表达式集合
     */
    protected List<String> preparedColumnList = null;

    /**
     * 主键预编译表达式集合
     */
    protected List<String> preparedPrimaryKeyList = null;

    /**
     * 主键集合
     */
    protected List<String> primaryKeyList = null;

    public String getSql(Struct key, Struct payload, String table) {
        sqlParameterMap = Maps.newHashMap();

        // 解析表主键
        if (needParsePrimaryKey()) {
            parsePrimaryKey(key);
        }

        if (needParseColumn()) {
            // 获取数据库cdc事件之后的值
            Struct afterValue = DebeziumRecordUtils.getRecordStructValue(payload, "after");

            // 处理表的列
            handleColumn(afterValue, getFieldPredicate(), getColumnNameFunction());
        }

        return generateSql(table);

    }

    protected Predicate<Field> getFieldPredicate() {
        return field -> true;
    }

    protected Function<String, String> getColumnNameFunction() {
        return columnName -> columnName;
    }

    protected abstract boolean needParseColumn();

    protected abstract boolean needParsePrimaryKey();

    protected abstract String generateSql(String table);

    /**
     * 解析表主键
     *
     * @param key 记录主键结果
     */
    protected void parsePrimaryKey(Struct key) {
        primaryKeyList = Lists.newArrayList();
        preparedPrimaryKeyList = Lists.newArrayList();

        key.schema().fields().stream().forEach(field -> {
            String primaryKey = field.name();
            preparedPrimaryKeyList.add(primaryKey + "= :" + primaryKey);
            primaryKeyList.add(primaryKey);

            sqlParameterMap.put(primaryKey, parseColumnValue(field, key.get(field)));
        });
    }

    /**
     * 处理表列
     *
     * @param predicate  是否需要过滤
     * @param function   列表名操作函数
     * @param afterValue
     */
    protected void handleColumn(Struct afterValue, Predicate<Field> predicate,
                                Function<String, String> function) {
        preparedColumnList = Lists.newArrayList();

        afterValue.schema().fields().stream()
                .filter(predicate)
                .forEach(field -> {
                    String columnName = field.name();
                    preparedColumnList.add(function.apply(columnName));

                    sqlParameterMap.put(columnName, parseColumnValue(field, afterValue.get(field)));
                });
    }

    /**
     * 解析表列的值
     *
     * @param field
     * @param value
     */
    protected Object parseColumnValue(Field field, Object value) {
        if (Objects.isNull(value)) {
            return null;
        }

        if (value instanceof ByteBuffer) {
           return CharUtils.getByte((ByteBuffer) value);
        }

        Schema schema = field.schema();
        DebeziumParser parser = ParserFactory.getParser(schema.name());
        if (Objects.nonNull(parser)) {
            return parser.parse(schema, value);
        }
        return value;
    }

    public Map<String, Object> getSqlParameterMap() {
        return sqlParameterMap;
    }
}