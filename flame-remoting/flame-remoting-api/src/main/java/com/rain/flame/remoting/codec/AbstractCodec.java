package com.rain.flame.remoting.codec;

import com.rain.flame.Channel;
import com.rain.flame.remoting.Codecs;
import com.rain.flame.remoting.buffer.ChannelBuffer;

import java.io.IOException;

public abstract class AbstractCodec implements Codecs {
    @Override
    public void encode(Channel channel, ChannelBuffer buffer, Object message) throws IOException {

    }

    @Override
    public abstract Object decode(Channel channel, ChannelBuffer buffer) throws IOException;
}
