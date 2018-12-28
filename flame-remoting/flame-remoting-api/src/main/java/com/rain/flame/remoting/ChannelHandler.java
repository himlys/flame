package com.rain.flame.remoting;

import com.rain.flame.Channel;

public interface ChannelHandler {
    void connected(Channel channel);

    void disconnected(Channel channel);

    void sent(Channel channel, Object message);

    void received(Channel channel, Object message);

    void caught(Channel channel, Throwable exception);
}