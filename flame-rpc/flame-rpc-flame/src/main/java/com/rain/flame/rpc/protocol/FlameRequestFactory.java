package com.rain.flame.rpc.protocol;

import com.rain.flame.common.URL;
import com.rain.flame.remoting.ChannelHandler;
import com.rain.flame.rpc.Protocol;
import com.rain.flame.rpc.client.RpcRequest;
import com.rain.flame.rpc.client.RpcRequestFactory;
import com.rain.flame.rpc.client.RpcRequestInterceptor;
import com.rain.flame.rpc.client.intercepting.InterceptingRpcRequestFactory;

import java.util.List;

public class FlameRequestFactory extends InterceptingRpcRequestFactory {

    public FlameRequestFactory(RpcRequestFactory requestFactory, List<RpcRequestInterceptor> interceptors) {
        super(requestFactory, interceptors);
    }

    @Override
    public RpcRequest createRequest(URL url, ChannelHandler handler, Protocol protocol) {
        return new FlameRequest(getRpcRequestFactory(), getRpcRequestInterceptors(), url, handler,protocol);
    }
}
