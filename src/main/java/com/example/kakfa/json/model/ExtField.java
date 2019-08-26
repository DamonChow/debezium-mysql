package com.example.kakfa.json.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 功能：
 *
 * @author Damon
 * @since 2019-04-23 19:58
 */
@Data
public class ExtField {

    private String type;

    private boolean optional;

    private String name;

    private int version;

    private Map<String, String> parameters;

    private String field;

    @JsonProperty("default")
    private String defaultValue;

    private List<ExtField> fields;

}
