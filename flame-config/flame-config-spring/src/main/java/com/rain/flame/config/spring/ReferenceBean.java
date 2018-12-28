package com.rain.flame.config.spring;

public class ReferenceBean<T> extends ReferenceConfig<T>{
    public Object getObject() {
        return get();
    }
}
