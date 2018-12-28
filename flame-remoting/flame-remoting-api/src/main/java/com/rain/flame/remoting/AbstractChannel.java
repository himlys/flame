package com.rain.flame.remoting;


import com.rain.flame.Channel;
import com.rain.flame.common.URL;

public abstract class AbstractChannel implements Channel {
    private final ChannelHandler handler;
    private final URL url;

    public ChannelHandler getHandler() {
        return handler;
    }

    public AbstractChannel(ChannelHandler handler, URL url) {
        this.handler = handler;
        this.url = url;
    }

    public URL getUrl() {
        return url;
    }

    public void send(Object msg) {
        doSend(msg);
    }

    protected abstract void doSend(Object msg);
}
