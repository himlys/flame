package com.rain.flame.common.utils;


import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ResourceLoader {
    public static Properties loadProperties(ClassLoader classLoader,String location) {
        Properties p = null;
        Enumeration<URL> urls = null;
        try {
            if(classLoader == null) {
                urls = ClassLoader.getSystemResources(location);
            }else{
                urls = classLoader.getResources(location);
            }
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                p = new Properties();
                p.load(url.openStream());
            }
            return p;
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to beanDefinition from location [" +
                    location + "]", ex);
        }
    }
}
