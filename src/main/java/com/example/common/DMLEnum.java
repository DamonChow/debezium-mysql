package com.example.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 功能：
 *
 * @author Damon
 * @since 2019-04-22 14:54
 */
@Getter
@AllArgsConstructor
public enum DMLEnum {

    INSERT_SQL(Constants.INSERT_SQL),
    UPDATE_SQL(Constants.UPDATE_SQL),
    DELETE_SQL(Constants.DELETE_SQL),
    ;

    private String sqlFormatter;

    public String generateSQL(Object...args) {
       return String.format(getSqlFormatter(), args);
    }
}
