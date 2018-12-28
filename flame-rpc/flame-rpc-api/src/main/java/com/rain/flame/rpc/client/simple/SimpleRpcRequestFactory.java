package com.rain.flame.rpc.client.simple;

import com.rain.flame.common.URL;
import com.rain.flame.remoting.ChannelHandler;
import com.rain.flame.rpc.Protocol;
import com.rain.flame.rpc.client.RpcRequest;
import com.rain.flame.rpc.client.RpcRequestFactory;
import com.rain.flame.rpc.client.RpcRequestInterceptor;

import java.util.ArrayList;
import java.util.List;

public class SimpleRpcRequestFactory implements RpcRequestFactory {
    public RpcRequest createRequest(URL url, ChannelHandler handler, Protocol protocol) {
        return new SimpleRpcRequest(url, handler, new ArrayList<>(), protocol);
    }

    public RpcRequest createRequest(URL url, ChannelHandler handler, List<RpcRequestInterceptor> rpcRequestInterceptors, Protocol protocol) {
        return new SimpleRpcRequest(url, handler, rpcRequestInterceptors, protocol);
    }
}
