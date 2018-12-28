package com.rain.flame.rpc.server;

import com.rain.flame.Channel;
import com.rain.flame.Factory;
import com.rain.flame.common.URL;

public interface RpcInvokerFactory extends Factory {
    public RpcInvoker createInvoker(Channel channel, URL url);
}
