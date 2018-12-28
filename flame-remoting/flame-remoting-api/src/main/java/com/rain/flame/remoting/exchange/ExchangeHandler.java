package com.rain.flame.remoting.exchange;

import com.rain.flame.Channel;
import com.rain.flame.remoting.ChannelHandler;

import java.util.concurrent.CompletableFuture;

public interface ExchangeHandler extends ChannelHandler {
    public CompletableFuture<Object> reply(Channel channel, Object request);
}
