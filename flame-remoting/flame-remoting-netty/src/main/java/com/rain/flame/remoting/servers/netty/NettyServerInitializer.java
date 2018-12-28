package com.rain.flame.remoting.servers.netty;

import com.rain.flame.common.URL;
import com.rain.flame.remoting.ChannelHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.ssl.SslContext;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;

@io.netty.channel.ChannelHandler.Sharable
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

    private final MessageToMessageDecoder<ByteBuf> DECODER = new MessageToMessageDecoder<ByteBuf>() {
        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List out) throws Exception {
            out.add(msg.toString(Charset.defaultCharset()));
        }
    };
    private final MessageToMessageEncoder ENCODER = new MessageToMessageEncoder<CharSequence>() {

        @Override
        protected void encode(ChannelHandlerContext ctx, CharSequence msg, List<Object> out) throws Exception {
            if (msg.length() == 0) {
                return;
            }
            out.add(ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(msg), Charset.defaultCharset()));
        }
    };
    private final URL url;
    private final NettyServerHandler SERVER_HANDLER;

    private final SslContext sslCtx;
    private NettyCodecAdapter adapter;
    private final ChannelHandler handler;

    public NettyServerInitializer(SslContext sslCtx, URL url, ChannelHandler handler) {
        this.sslCtx = sslCtx;
        this.handler = handler;
        this.SERVER_HANDLER = new NettyServerHandler(handler, url);
        this.url = url;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        this.adapter = new NettyCodecAdapter(handler, url);
        ChannelPipeline pipeline = ch.pipeline();

        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }

        // the encoder and decoder are static as these are sharable
        pipeline.addLast(adapter.getDecoder());
        pipeline.addLast(adapter.getEncoder());

        // and then business logic.
        pipeline.addLast(SERVER_HANDLER);
    }
}
