package com.example.kakfa.json.model;

import lombok.Data;

import java.util.List;

/**
 * 功能：
 *
 * @author Damon
 * @since 2019-04-23 19:54
 */
@Data
public class ExtSchema {

    /**
     * @return the type of this schema
     */
//    private org.apache.kafka.connect.data.ExtSchema.Type type;

    private String type;

    private boolean optional;

    private String name;

    private List<ExtField> fields;

}
