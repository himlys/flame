package com.rain.flame.common.utils;

import org.springframework.context.ApplicationContext;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class SystemBeanUtils {
    public static final Map<String, String> map = new ConcurrentHashMap<>();
    public static final String FLAME_FACTORIES_RESOURCE_LOCATION = "META-INF/internal/defaultBeanConfig.properties";

    public static <T> T get(Class<T> classz) {
        return get(classz, null);
    }

    public static <T> T get(Class<T> classz, String name) {
        if (name == null || "".equals(name)) {
            name = "default" + classz.getSimpleName();
            ApplicationContext context = ApplicationContextHelper.getApplicationContext();
            if (map.isEmpty()) {
                Properties p = loadProperties();
                for (Iterator it = p.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry entry = (Map.Entry) it.next();
                    map.put((String) entry.getKey(), (String) entry.getValue());
                }
            }
            String classname = map.get(name);
            Class instanceClass = null;
            try {
                instanceClass = Class.forName(classname);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (context == null) {
                try {
                    return (T) instanceClass.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                return SpringBeanUtils.getBean(StringUtils.upperFirstChar(instanceClass.getSimpleName()));
            }

        }
        return SpringBeanUtils.getBean(name);
    }

    private static Properties loadProperties() {
        return ResourceLoader.loadProperties(findClassLoader("com.rain.flame.starter.config.autoconfiguration.CodecAutoConfiguration"), FLAME_FACTORIES_RESOURCE_LOCATION);
    }

    private static ClassLoader findClassLoader(String className) {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
        }
        try {
            Class clazz = Class.forName(className);
            if (cl == null) {
                cl = clazz.getClassLoader();
                if (cl == null) {
                    try {
                        cl = ClassLoader.getSystemClassLoader();
                    } catch (Throwable ex) {
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return cl;
    }
}
