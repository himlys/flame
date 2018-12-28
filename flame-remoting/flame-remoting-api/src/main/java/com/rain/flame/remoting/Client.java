package com.rain.flame.remoting;

import com.rain.flame.common.URL;

import java.util.concurrent.atomic.AtomicInteger;

public interface Client {
    Object send(Object param);

    boolean isActive();

    URL getUrl();

    void reconnect();

    AtomicInteger getReconnectCount();
}
