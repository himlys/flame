package com.rain.flame.common.utils;

import org.springframework.core.env.Environment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class PropertyUtil {

    public static <T> T handle(final Environment environment, final String prefix, final Class<T> targetClass) {
        Class<?> binderClass = null;
        try {
            binderClass = Class.forName("org.springframework.boot.context.properties.bind.Binder");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("请使用springboot2.x版本");
        }
        try {
            Method getMethod = null;
            getMethod = binderClass.getDeclaredMethod("get", Environment.class);
            Method bindMethod = null;
            bindMethod = binderClass.getDeclaredMethod("bind", String.class, Class.class);
            Object binderObject = null;
            binderObject = getMethod.invoke(null, environment);
            String prefixParam = prefix.endsWith(".") ? prefix.substring(0, prefix.length() - 1) : prefix;
            Object bindResultObject = null;
            bindResultObject = bindMethod.invoke(binderObject, prefixParam, targetClass);
            Method resultGetMethod = null;
            resultGetMethod = bindResultObject.getClass().getDeclaredMethod("get");
            return (T) resultGetMethod.invoke(bindResultObject);
        } catch (NoSuchMethodException e) {

        } catch (IllegalAccessException e) {

        } catch (InvocationTargetException e) {

        }

        return null;
    }
}