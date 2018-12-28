package com.rain.flame.registry.api;

import com.rain.flame.Request;
import com.rain.flame.common.URL;
import org.springframework.context.ApplicationContext;

public interface Registry {
    void register(URL url);

    void unregister(URL url);

    void subscribe(URL url, Request request);

    void unsubscribe(URL url);

    void setApplicationContext(ApplicationContext applicationContext);
}
