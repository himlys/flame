package com.rain.flame.remoting;

import com.rain.flame.common.URL;
import com.rain.flame.remoting.Server;
import com.rain.flame.remoting.ChannelHandler;

import java.net.InetSocketAddress;

public abstract class AbstractServer implements Server {
    private InetSocketAddress bindAddress;
    private final ChannelHandler handler;
    private final URL url;

    protected AbstractServer(ChannelHandler handler, URL url) {
        this.handler = handler;
        this.url = url;
        this.bindAddress = new InetSocketAddress(url.getHost(),url.getPort());
        doOpen();
    }

    public URL getUrl() {
        return url;
    }

    public ChannelHandler getHandler() {
        return handler;
    }

    public InetSocketAddress getBindAddress() {
        return bindAddress;
    }

    protected abstract void doOpen();

    protected abstract void doClose() throws Throwable;
}
