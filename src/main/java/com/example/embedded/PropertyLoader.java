/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package com.example.embedded;

import lombok.extern.slf4j.Slf4j;

import java.util.Enumeration;
import java.util.Properties;


@Slf4j
public class PropertyLoader {

    public static void loadEnvironmentValues(Properties properties) {
        Enumeration<?> keys = properties.propertyNames();

        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            String envSafeKey = key.replace(".", "_");
            envSafeKey = envSafeKey.replace("-", "_");
            String envValue = System.getenv(envSafeKey.toUpperCase());
            String systemPropValue = System.getProperty(key.toUpperCase());
            String oldValue = properties.getProperty(key);

            if (envValue != null) {
                properties.setProperty(key, envValue);
                log.debug("Setting java property for key:" + key + " , value:" + envValue + " , oldValue:" + oldValue);
            }
            else if (systemPropValue != null) {
                properties.setProperty(key, systemPropValue);
                log.debug("Setting java property for key:" + key + " ,v alue:" + systemPropValue + " , oldValue:"
                        + oldValue);
            }
        }
    }

}
