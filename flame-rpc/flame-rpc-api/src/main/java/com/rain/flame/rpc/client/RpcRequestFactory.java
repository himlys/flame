package com.rain.flame.rpc.client;

import com.rain.flame.Factory;
import com.rain.flame.common.URL;
import com.rain.flame.remoting.ChannelHandler;
import com.rain.flame.rpc.Protocol;

import java.util.List;

public interface RpcRequestFactory extends Factory {
    RpcRequest createRequest(URL url, ChannelHandler handler, Protocol protocol);

    RpcRequest createRequest(URL url, ChannelHandler handler, List<RpcRequestInterceptor> rpcRequestInterceptors, Protocol protocol);
}
