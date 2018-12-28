package com.rain.flame.remoting.servers.netty;

import com.rain.flame.Channel;
import com.rain.flame.common.URL;
import com.rain.flame.remoting.AbstractServer;
import com.rain.flame.remoting.ChannelHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;
import java.util.Map;

public class NettyServer extends AbstractServer {

    private Map<String, Channel> channels;
    private ServerBootstrap bootstrap;
    private io.netty.channel.Channel channel;
    static final boolean SSL = System.getProperty("ssl") != null;

    public NettyServer(ChannelHandler handler, URL url) {
        super(handler, url);
    }

    @Override
    protected void doOpen() {
// Configure SSL.
        SslContext sslCtx = null;
        EventLoopGroup bossGroup = new NioEventLoopGroup(3);
        EventLoopGroup workerGroup = new NioEventLoopGroup(2);
        try {
            if (SSL) {
                SelfSignedCertificate ssc = null;

                ssc = new SelfSignedCertificate();
                sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
            } else {
                sslCtx = null;
            }
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (SSLException e) {
            e.printStackTrace();
        }
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new NettyServerInitializer(sslCtx, getUrl(), getHandler()));

//            b.bind(PORT).sync().channel().closeFuture().sync();
        ChannelFuture future = b.bind(getUrl().getPort());
        future.syncUninterruptibly();
        channel = future.channel();
    }

    @Override
    protected void doClose() throws Throwable {

    }
}
