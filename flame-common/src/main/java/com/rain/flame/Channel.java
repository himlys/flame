package com.rain.flame;


import com.rain.flame.common.URL;

public interface Channel {
    void send(Object msg);

    URL getUrl();

    boolean isConnected();
}