package com.rain.flame.rpc.server.intercepting;

import com.rain.flame.Channel;
import com.rain.flame.Invocation;
import com.rain.flame.common.URL;
import com.rain.flame.rpc.RpcInvocation;
import com.rain.flame.rpc.server.*;

import java.util.Iterator;
import java.util.List;

public class InterceptingRpcInvoker extends AbstractRpcInvoker {
    private final List<RpcInvokeInterceptor> interceptors;
    private final RpcInvokerFactory rpcInvokerFactory;

    public InterceptingRpcInvoker(RpcInvokerFactory rpcInvokerFactory, List<RpcInvokeInterceptor> interceptors, Channel channel, URL url, Object target) {
        super(channel, url, target);
        this.interceptors = interceptors;
        this.rpcInvokerFactory = rpcInvokerFactory;

    }

    @Override
    public RpcInvokeResult invoke(RpcInvocation invocation) {
        InterceptingRpcInvokerExecution execution = new InterceptingRpcInvokerExecution();
        return execution.execute(invocation);
    }

    private class InterceptingRpcInvokerExecution implements RpcInvokerExecution {
        private final Iterator<RpcInvokeInterceptor> iterator;

        public InterceptingRpcInvokerExecution() {
            this.iterator = interceptors.iterator();
        }

        @Override
        public RpcInvokeResult execute(RpcInvocation invocation) {
            if (this.iterator.hasNext()) {
                RpcInvokeInterceptor nextInterceptor = this.iterator.next();
                return nextInterceptor.intercept(invocation, this);
            } else {
                RpcInvoker delegate = rpcInvokerFactory.createInvoker(getChannel(), getUrl());
                return delegate.invoke(invocation);
            }
        }
    }
}
