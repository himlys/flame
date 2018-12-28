package com.rain.flame.remoting.servers.netty;

import com.rain.flame.common.URL;
import com.rain.flame.common.utils.SystemBeanUtils;
import com.rain.flame.remoting.ChannelHandler;
import com.rain.flame.remoting.Codecs;
import com.rain.flame.remoting.buffer.ChannelBuffer;
import com.rain.flame.rpc.protocol.FlameCodec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.List;
@io.netty.channel.ChannelHandler.Sharable
public class NettyCodecAdapter {
    private final Codecs codec;
    private final URL url;
    private final ChannelHandler handler;
    private final InternalDecoder decoder = new InternalDecoder();
    private final InternalEncoder encoder = new InternalEncoder();

    public NettyCodecAdapter(ChannelHandler handler, URL url) {
        this.codec = SystemBeanUtils.get(Codecs.class);
        this.url = url;
        this.handler = handler;
    }

    private class InternalEncoder extends MessageToByteEncoder {

        @Override
        protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
            ChannelBuffer buffer = new NettyBackedChannelBuffer(out);
            Channel ch = ctx.channel();
            NettyChannel channel = NettyChannel.getOrAddChannel(ch, url, handler);
            try {
                codec.encode(channel, buffer, msg);
            } catch (Exception e){
                e.printStackTrace();
            } finally{

            }
        }
    }

    private class InternalDecoder extends ByteToMessageDecoder {

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            ChannelBuffer message = new NettyBackedChannelBuffer(in);
            NettyChannel channel = NettyChannel.getOrAddChannel(ctx.channel(), url, handler);
            Object msg;
            try {
                msg = codec.decode(channel, message);
                out.add(msg);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public InternalDecoder getDecoder() {
        return decoder;
    }

    public InternalEncoder getEncoder() {
        return encoder;
    }
}
