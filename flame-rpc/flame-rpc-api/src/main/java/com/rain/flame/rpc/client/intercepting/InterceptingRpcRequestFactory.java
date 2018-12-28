package com.rain.flame.rpc.client.intercepting;

import com.rain.flame.common.URL;
import com.rain.flame.remoting.ChannelHandler;
import com.rain.flame.rpc.Protocol;
import com.rain.flame.rpc.client.RpcRequest;
import com.rain.flame.rpc.client.RpcRequestFactory;
import com.rain.flame.rpc.client.RpcRequestInterceptor;
import org.springframework.lang.Nullable;

import java.util.Collections;
import java.util.List;

public class InterceptingRpcRequestFactory extends AbstractRpcRequestFactoryWrapper {
    private final List<RpcRequestInterceptor> rpcRequestInterceptors;

    public InterceptingRpcRequestFactory(RpcRequestFactory requestFactory,
                                         @Nullable List<RpcRequestInterceptor> rpcRequestInterceptors) {

        super(requestFactory);
        this.rpcRequestInterceptors = (rpcRequestInterceptors != null ? rpcRequestInterceptors : Collections.emptyList());
    }

    @Override
    public RpcRequest createRequest(URL url, ChannelHandler handler, Protocol protocol) {
        return new InterceptingRpcRequest(getRpcRequestFactory(), rpcRequestInterceptors, url, handler, protocol);
    }

    public RpcRequest createRequest(URL url, ChannelHandler handler, List<RpcRequestInterceptor> rpcRequestInterceptors, Protocol protocol) {
        return new InterceptingRpcRequest(getRpcRequestFactory(), rpcRequestInterceptors, url, handler, protocol);
    }

    public List<RpcRequestInterceptor> getRpcRequestInterceptors() {
        return rpcRequestInterceptors;
    }

}
