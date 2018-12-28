package com.rain.flame.remoting.servers.netty;

import com.rain.flame.common.URL;
import com.rain.flame.remoting.AbstractChannel;
import com.rain.flame.remoting.ChannelHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class NettyChannel extends AbstractChannel {
    private Channel channel;
    private static final ConcurrentMap<Channel, NettyChannel> channelMap = new ConcurrentHashMap<Channel, NettyChannel>();

    public NettyChannel(Channel channel, URL url, ChannelHandler channelHandler) {
        super(channelHandler, url);
        this.channel = channel;
    }

    static NettyChannel getOrAddChannel(Channel ch, URL url, ChannelHandler handler) {
        if (ch == null) {
            return null;
        }
        NettyChannel ret = channelMap.get(ch);
        if (ret == null) {
            NettyChannel nettyChannel = new NettyChannel(ch, url, handler);
            if (ch.isActive()) {
                ret = channelMap.putIfAbsent(ch, nettyChannel);
            }
            if (ret == null) {
                ret = nettyChannel;
            }
        }
        return ret;
    }

    protected void doSend(Object message) {
        boolean success = true;
        int timeout = 0;
        try {
            ChannelFuture future = channel.writeAndFlush(message);
            timeout = 6000;
            success = future.await(timeout);
            Throwable cause = future.cause();
            if (cause != null) {
                throw cause;
            }
        } catch (Throwable e) {
            throw new RuntimeException("Failed to send message " + message + " to " + getRemoteAddress() + ", cause: " + e.getMessage());
        }

        if (!success) {
            throw new RuntimeException("Failed to send message " + message + " to " + getRemoteAddress()
                    + "in timeout(" + timeout + "ms) limit");
        }
    }

    public InetSocketAddress getRemoteAddress() {
        return (InetSocketAddress) channel.remoteAddress();
    }

    @Override
    public boolean isConnected() {
        return channel.isActive();
    }

    static void removeChannelIfDisconnected(Channel ch) {
        if (ch != null && !ch.isActive()) {
            channelMap.remove(ch);
        }
    }
}
