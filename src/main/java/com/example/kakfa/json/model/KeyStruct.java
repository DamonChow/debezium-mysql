package com.example.kakfa.json.model;

import lombok.Data;

import java.util.Map;

/**
 * 功能：
 *
 * @author Damon
 * @since 2019-04-23 19:58
 */
@Data
public class KeyStruct {

    private ExtSchema schema;

    private Map<String, Object> payload;
}
