package com.example.pulsar;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 功能：
 *
 * @author Damon
 * @since 2019-04-15 17:17
 */
@Data
public class Key {

//    {
//        "schema": {
//        "type": "struct",
//                "fields": [{
//            "type": "int32",
//                    "optional": false,
//                    "field": "id"
//        }],
//        "optional": false,
//                "name": "dbserver1.inventory.products.Key"
//    },
//        "payload": {
//        "id": 109
//    }
//    }

    private Schema schema;

    private Map<String, Object> payload;

    @Data
    static class Schema {
        private String type;

        private List<Field> fields;

        private boolean optional;

        private String name;

    }

    @Data
    static class Field {

        private String type;

        private boolean optional;

        private String field;
    }
}
