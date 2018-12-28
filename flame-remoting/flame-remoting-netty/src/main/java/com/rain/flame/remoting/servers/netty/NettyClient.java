package com.rain.flame.remoting.servers.netty;

import com.rain.flame.common.URL;
import com.rain.flame.remoting.AbstractClient;
import com.rain.flame.remoting.ChannelHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class NettyClient extends AbstractClient {
    static final boolean SSL = System.getProperty("ssl") != null;
    Bootstrap b;
    private volatile Channel channel;

    public NettyClient(URL url, ChannelHandler handler) {
        super(url, handler);
    }

    @Override
    protected void doOpen() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // Configure SSL.
            final SslContext sslCtx;
            if (SSL) {
                sslCtx = SslContextBuilder.forClient()
                        .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
            } else {
                sslCtx = null;
            }

            b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new NettyClientInitializer(sslCtx, getUrl(), getHandler()));

            // Start the connection attempt.
        } catch (SSLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doConnect() {
        ChannelFuture future = b.connect(getConnectAddress());
        boolean ret = future.awaitUninterruptibly(getConnectTimeout(), TimeUnit.MILLISECONDS);
        if (ret && future.isSuccess()) {
            Channel newChannel = future.channel();
            try {
                // Close old channel
                Channel oldChannel = NettyClient.this.channel; // copy reference
                if (oldChannel != null) {
                    try {
                        oldChannel.close();
                    } finally {
                        NettyChannel.removeChannelIfDisconnected(oldChannel);
                    }
                }
            } catch (Exception e) {

            } finally {
                if (NettyClient.this.isClosed()) {
                    try {
                        newChannel.close();
                    } finally {
                        NettyClient.this.channel = null;
                        NettyChannel.removeChannelIfDisconnected(newChannel);
                    }
                } else {
                    NettyClient.this.channel = newChannel;
                }
            }
        }
    }

    @Override
    protected com.rain.flame.Channel getChannel() {
        Channel c = channel;
        if (c == null || !c.isActive()) {
            return null;
        }
        return NettyChannel.getOrAddChannel(c, getUrl(), getHandler());
    }

    @Override
    protected void doReconnect() {
        doOpen();
        doConnect();
    }

    @Override
    public Object send(Object requestBody) {
        channel.writeAndFlush(requestBody);
        return null;
    }

    @Override
    public boolean isActive() {
        if (channel == null) return false;
        return channel.isActive();
    }
}
