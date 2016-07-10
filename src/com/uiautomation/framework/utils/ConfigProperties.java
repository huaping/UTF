package com.uiautomation.framework.utils;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Properties;

public class ConfigProperties {
    private static final HashMap<String, Properties> PROP_MAPS = new HashMap<String, Properties>();


    public static final String PROPERTIES_FILENAME = "/data/local/tmp/uiautotest.properties";

    public static final String FILE_TYPE = "properties";

    public static Properties tryToLoadPropertyFile(String fileName, String type) {
        Properties props;
        if (!PROP_MAPS.containsKey(fileName)) {
            props = new Properties();
            PROP_MAPS.put(fileName, props);
            try {
                if ("xml".equals(type)) {
                    props.loadFromXML(new FileInputStream(fileName));
                } else {
                    props.load(new FileInputStream(fileName));
                }
            } catch (Exception ex) {

            }
        }
        return PROP_MAPS.get(fileName);
    }

    private static Properties props;
    static {
        props = tryToLoadPropertyFile(PROPERTIES_FILENAME, FILE_TYPE);
    }

    public static String getString(String name) {
        return props.getProperty(name, "");
    }

    public static int getInt(String name) {
        int value = 0;
        try {
            value = Integer.parseInt(getString(name));
        } catch (Exception ex) {
        }
        return value;
    }
}
