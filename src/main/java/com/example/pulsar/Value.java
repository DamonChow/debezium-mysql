package com.example.pulsar;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 功能：
 *
 * @author Damon
 * @since 2019-04-15 17:22
 */
@Data
public class Value {

    /**
     * schema : {"type":"struct","fields":[{"type":"struct","fields":[{"type":"int32","optional":false,"field":"id"},{"type":"string","optional":false,"field":"name"},{"type":"string","optional":true,"field":"description"},{"type":"double","optional":true,"field":"weight"}],"optional":true,"name":"dbserver1.inventory.products.Value","field":"before"},{"type":"struct","fields":[{"type":"int32","optional":false,"field":"id"},{"type":"string","optional":false,"field":"name"},{"type":"string","optional":true,"field":"description"},{"type":"double","optional":true,"field":"weight"}],"optional":true,"name":"dbserver1.inventory.products.Value","field":"after"},{"type":"struct","fields":[{"type":"string","optional":true,"field":"version"},{"type":"string","optional":false,"field":"name"},{"type":"int64","optional":false,"field":"server_id"},{"type":"int64","optional":false,"field":"ts_sec"},{"type":"string","optional":true,"field":"gtid"},{"type":"string","optional":false,"field":"file"},{"type":"int64","optional":false,"field":"pos"},{"type":"int32","optional":false,"field":"row"},{"type":"boolean","optional":true,"default":false,"field":"snapshot"},{"type":"int64","optional":true,"field":"thread"},{"type":"string","optional":true,"field":"db"},{"type":"string","optional":true,"field":"table"},{"type":"string","optional":true,"field":"query"}],"optional":false,"name":"io.debezium.connector.mysql.Source","field":"source"},{"type":"string","optional":false,"field":"op"},{"type":"int64","optional":true,"field":"ts_ms"}],"optional":false,"name":"dbserver1.inventory.products.Envelope"}
     * payload : {"before":{"id":109,"name":"spare tire","description":"24 inch spare tire","weight":22.200000762939453},"after":{"id":109,"name":"spare tire","description":"24 inch spare tire0","weight":22.200000762939453},"source":{"version":"0.8.2","name":"dbserver1","server_id":223344,"ts_sec":1555319121,"gtid":null,"file":"mysql-bin.000003","pos":7446,"row":0,"snapshot":false,"thread":2,"db":"inventory","table":"products","query":null},"op":"u","ts_ms":1555319121063}
     */

    private Schema schema;

    private Payload payload;

    @Data
    public static class Schema {
        /**
         * type : struct
         * fields : [{"type":"struct","fields":[{"type":"int32","optional":false,"field":"id"},{"type":"string","optional":false,"field":"name"},{"type":"string","optional":true,"field":"description"},{"type":"double","optional":true,"field":"weight"}],"optional":true,"name":"dbserver1.inventory.products.Value","field":"before"},{"type":"struct","fields":[{"type":"int32","optional":false,"field":"id"},{"type":"string","optional":false,"field":"name"},{"type":"string","optional":true,"field":"description"},{"type":"double","optional":true,"field":"weight"}],"optional":true,"name":"dbserver1.inventory.products.Value","field":"after"},{"type":"struct","fields":[{"type":"string","optional":true,"field":"version"},{"type":"string","optional":false,"field":"name"},{"type":"int64","optional":false,"field":"server_id"},{"type":"int64","optional":false,"field":"ts_sec"},{"type":"string","optional":true,"field":"gtid"},{"type":"string","optional":false,"field":"file"},{"type":"int64","optional":false,"field":"pos"},{"type":"int32","optional":false,"field":"row"},{"type":"boolean","optional":true,"default":false,"field":"snapshot"},{"type":"int64","optional":true,"field":"thread"},{"type":"string","optional":true,"field":"db"},{"type":"string","optional":true,"field":"table"},{"type":"string","optional":true,"field":"query"}],"optional":false,"name":"io.debezium.connector.mysql.Source","field":"source"},{"type":"string","optional":false,"field":"op"},{"type":"int64","optional":true,"field":"ts_ms"}]
         * optional : false
         * name : dbserver1.inventory.products.Envelope
         */

        private String type;

        private boolean optional;

        private String name;

        private List<FieldStruct> fields;

        @Data
        public static class FieldStruct {
            /**
             * type : struct
             * fields : [{"type":"int32","optional":false,"field":"id"},{"type":"string","optional":false,"field":"name"},{"type":"string","optional":true,"field":"description"},{"type":"double","optional":true,"field":"weight"}]
             * optional : true
             * name : dbserver1.inventory.products.Value
             * field : before
             */

            private String type;

            private boolean optional;

            private String name;

            private String field;

            private List<Field> fields;

            @Data
            public static class Field {
                /**
                 * type : int32
                 * optional : false
                 * field : id
                 */

                private String type;
                private boolean optional;
                private String field;

            }
        }
    }

    @Data
    public static class Payload {
        /**
         * before : {"id":109,"name":"spare tire","description":"24 inch spare tire","weight":22.200000762939453}
         * after : {"id":109,"name":"spare tire","description":"24 inch spare tire0","weight":22.200000762939453}
         * source : {"version":"0.8.2","name":"dbserver1","server_id":223344,"ts_sec":1555319121,"gtid":null,"file":"mysql-bin.000003","pos":7446,"row":0,"snapshot":false,"thread":2,"db":"inventory","table":"products","query":null}
         * op : u
         * ts_ms : 1555319121063
         */

        private Map<String, Object> before;
        private Map<String, Object> after;
        private Source source;
        private String op;
        private long ts_ms;

        @Data
        public static class Before {
            /**
             * id : 109
             * name : spare tire
             * description : 24 inch spare tire
             * weight : 22.200000762939453
             */

            private int id;
            private String name;
            private String description;
            private double weight;

        }

        @Data
        public static class After {
            /**
             * id : 109
             * name : spare tire
             * description : 24 inch spare tire0
             * weight : 22.200000762939453
             */

            private int id;
            private String name;
            private String description;
            private double weight;

        }

        @Data
        public static class Source {
            /**
             * version : 0.8.2
             * name : dbserver1
             * server_id : 223344
             * ts_sec : 1555319121
             * gtid : null
             * file : mysql-bin.000003
             * pos : 7446
             * row : 0
             * snapshot : false
             * thread : 2
             * db : inventory
             * table : products
             * query : null
             */

            private String version;
            private String name;
            private int server_id;
            private int ts_sec;
            private Object gtid;
            private String file;
            private int pos;
            private int row;
            private boolean snapshot;
            private int thread;
            private String db;
            private String table;
            private Object query;
        }
    }
}
