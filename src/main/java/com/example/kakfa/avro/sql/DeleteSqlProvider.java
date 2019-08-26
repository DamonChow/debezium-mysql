package com.example.kakfa.avro.sql;

import com.example.common.DMLEnum;
import org.apache.commons.lang3.StringUtils;

/**
 * 功能：
 *
 * @author Damon
 * @since 2019-04-22 15:13
 */
public class DeleteSqlProvider extends SqlProvider {

    @Override
    protected boolean needParseColumn() {
        return false;
    }

    @Override
    protected boolean needParsePrimaryKey() {
        return true;
    }

    @Override
    protected String generateSql(String table) {
        return DMLEnum.DELETE_SQL.generateSQL(table, StringUtils.join(preparedPrimaryKeyList, " and "));
    }
}
