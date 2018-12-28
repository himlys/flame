package com.rain.flame.remoting;

import com.rain.flame.Channel;
import com.rain.flame.common.URL;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractClient implements Client {
    private final ChannelHandler handler;
    private volatile boolean closed;
    private volatile URL url;
    private int connectTimeout;
    private final Lock connectLock = new ReentrantLock();
    private final AtomicInteger reconnectCount = new AtomicInteger();
    public URL getUrl() {
        return url;
    }

    public AbstractClient(URL url, ChannelHandler handler) {
        this.url = url;
        this.handler = handler;
        connectTimeout = 3000;
        doOpen();
        connect();
    }

    protected abstract void doOpen();

    public AtomicInteger getReconnectCount() {
        return reconnectCount;
    }

    protected abstract void doConnect();

    protected abstract Channel getChannel();

    protected void connect() {
        connectLock.lock();
        try {
            if (isConnected()) {
                return;
            }
            doConnect();
        }finally {
            connectLock.unlock();
        }
    }
    public void reconnect(){
        connectLock.lock();
        reconnectCount.getAndIncrement();
        try {
            doReconnect();
        }finally {
            connectLock.unlock();
        }
    }

    protected abstract void doReconnect();

    public boolean isConnected() {
        Channel channel = getChannel();
        if (channel == null) {
            return false;
        }
        return channel.isConnected();
    }

    public ChannelHandler getHandler() {
        return handler;
    }

    public void close() {
        closed = true;
    }

    public boolean isClosed() {
        return closed;
    }

    protected int getConnectTimeout() {
        return connectTimeout;
    }

    public InetSocketAddress getConnectAddress() {
        return new InetSocketAddress(url.getHost(), url.getPort());
    }
}
