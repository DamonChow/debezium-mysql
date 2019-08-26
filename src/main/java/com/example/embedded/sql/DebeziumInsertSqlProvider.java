package com.example.embedded.sql;

import com.example.common.DMLEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

/**
 * 功能：
 *
 * @author Damon
 * @since 2019-04-22 15:13
 */
public class DebeziumInsertSqlProvider extends AbstractDebeziumSqlProvider {

    @Override
    protected boolean needParseColumn() {
        return true;
    }

    @Override
    protected boolean needParsePrimaryKey() {
        return false;
    }

    @Override
    protected Function<String, String> getColumnNameFunction() {
        return columnName -> ":" + columnName;
    }

    @Override
    protected String generateSql(String table) {
        return DMLEnum.INSERT_SQL.generateSQL(table, StringUtils.join(preparedColumnList, ","));
    }
}
