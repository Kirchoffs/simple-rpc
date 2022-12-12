package org.syh.prj.rpc.simplerpc.core.common.config;

import org.syh.prj.rpc.simplerpc.core.common.utils.CommonUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesLoader {
    private static Properties properties;

    private static Map<String, String> propertiesMap = new HashMap<>();

    private static String DEFAULT_PROPERTIES_FILE = "application.properties";

    public static void loadConfiguration() throws IOException {
        if (properties != null) {
            return;
        }
        properties = new Properties();

        try {
            properties.load(PropertiesLoader.class.getClassLoader().getResourceAsStream(DEFAULT_PROPERTIES_FILE));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getPropertiesStr(String key) {
        return String.valueOf(getProperties(key));
    }

    public static Integer getPropertiesInteger(String key) {
        return Integer.valueOf(getProperties(key));
    }

    private static String getProperties(String key) {
        if (properties == null) {
            return null;
        }

        if (CommonUtils.isEmpty(key)) {
            return null;
        }

        if (!propertiesMap.containsKey(key)) {
            String value = properties.getProperty(key);
            propertiesMap.put(key, value);
        }

        return propertiesMap.get(key);
    }
}
