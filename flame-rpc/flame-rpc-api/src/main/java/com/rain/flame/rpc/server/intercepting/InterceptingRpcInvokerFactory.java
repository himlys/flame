package com.rain.flame.rpc.server.intercepting;

import com.rain.flame.Channel;
import com.rain.flame.common.URL;
import com.rain.flame.rpc.server.RpcInvokeInterceptor;
import com.rain.flame.rpc.server.RpcInvoker;
import com.rain.flame.rpc.server.RpcInvokerFactory;
import org.springframework.lang.Nullable;

import java.util.Collections;
import java.util.List;

public class InterceptingRpcInvokerFactory<T> extends AbstractRpcInvokerFactoryWrapper {
    private final List<RpcInvokeInterceptor> rpcInvokeInterceptors;

    public InterceptingRpcInvokerFactory(RpcInvokerFactory invokerFactory,
                                         @Nullable List<RpcInvokeInterceptor> rpcInvokeInterceptors) {

        super(invokerFactory);
        this.rpcInvokeInterceptors = (rpcInvokeInterceptors != null ? rpcInvokeInterceptors : Collections.emptyList());
    }

    @Override
    public RpcInvoker createInvoker(Channel channel, URL url) {
        return new InterceptingRpcInvoker(getRpcInvokerFactory(), rpcInvokeInterceptors, channel, url, null);
    }

    public List<RpcInvokeInterceptor> getInterceptors() {
        return rpcInvokeInterceptors;
    }
}
