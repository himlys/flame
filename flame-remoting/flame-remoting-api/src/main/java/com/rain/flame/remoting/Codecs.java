package com.rain.flame.remoting;

import com.rain.flame.Channel;
import com.rain.flame.remoting.buffer.ChannelBuffer;

import java.io.IOException;

public interface Codecs {
    void encode(Channel channel, ChannelBuffer buffer, Object message) throws IOException;

    Object decode(Channel channel, ChannelBuffer buffer) throws IOException;
}
