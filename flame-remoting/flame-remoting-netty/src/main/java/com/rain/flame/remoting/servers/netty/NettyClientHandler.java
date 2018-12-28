package com.rain.flame.remoting.servers.netty;

import com.rain.flame.common.URL;
import com.rain.flame.remoting.exchange.ExchangeInvocationResult;
import com.rain.flame.rpc.client.DefaultRpcResponse;
import com.rain.flame.rpc.server.RpcInvokeResult;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NettyClientHandler extends SimpleChannelInboundHandler<Object> {
    private final com.rain.flame.remoting.ChannelHandler handler;
    private final URL url;

    public NettyClientHandler(com.rain.flame.remoting.ChannelHandler handler, URL url) {
        this.handler = handler;
        this.url = url;
    }

    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelInactive();
        System.err.println("You have disconnect from " + ctx.channel().remoteAddress());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ExchangeInvocationResult) {
            msg = ((ExchangeInvocationResult) msg).getData();
            if (msg instanceof RpcInvokeResult) {
                DefaultRpcResponse.received((RpcInvokeResult) msg);
            } else if (msg instanceof String) {
                System.err.println(msg);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
