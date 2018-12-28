package com.rain.flame.rpc.protocol;

import com.rain.flame.Channel;
import com.rain.flame.Invoker;
import com.rain.flame.common.URL;
import com.rain.flame.remoting.exchange.*;
import com.rain.flame.remoting.exchange.server.DefaultExchangeServer;
import com.rain.flame.rpc.AbstractProtocol;
import com.rain.flame.rpc.RpcInvocation;
import com.rain.flame.rpc.client.RpcRequest;
import com.rain.flame.rpc.client.RpcRequestFactory;
import com.rain.flame.rpc.client.RpcRequestInterceptor;
import com.rain.flame.rpc.server.RpcInvokeInterceptor;
import com.rain.flame.rpc.server.RpcInvokerFactory;
import com.rain.flame.rpc.server.intercepting.InterceptingRpcInvokerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FlameProtocol extends AbstractProtocol {
    public FlameProtocol(List<RpcRequestInterceptor> requestInterceptors, List<RpcInvokeInterceptor> invokeInterceptors) {
        super(requestInterceptors, invokeInterceptors);
    }

    @Override
    public <T> RpcRequest refer(Class<T> serviceType, URL url) {
        RpcRequest request = getRequestFactory().createRequest(url, requestHandler, this);
        getRequests().put(url, request);
        return request;
    }

    private ExchangeHandler requestHandler = new ExchangeHandlerAdapter() {
        @Override
        public void connected(Channel channel) {

        }

        @Override
        public void disconnected(Channel channel) {

        }

        @Override
        public void sent(Channel channel, Object message) {

        }

        @Override
        public void received(Channel channel, Object message) {
            if (message instanceof ExchangeInvocation) {
                reply(channel, ((ExchangeInvocation) message).getData());
            } else {
                super.received(channel, message);
            }

        }

        @Override
        public void caught(Channel channel, Throwable exception) {

        }

        @Override
        public CompletableFuture<Object> reply(Channel channel, Object body) {
            if (body instanceof RpcInvocation) {
                ((RpcInvocation) body).setUrl(((RpcInvocation) body).getUrl());
                ExchangeInvoker invoker = getExchangeInvokerFactory().createExchangeInvoker(((RpcInvocation) body).getUrl());
                Invoker invoker1 = getInvokerFactory().createInvoker(channel, ((RpcInvocation) body).getUrl());
                ExchangeInvocationResult result = invoker.execute(invoker1, (RpcInvocation) body);
                return CompletableFuture.completedFuture(result);
            }

            return null;
        }
    };

    public ExchangeHandler getRequestHandler() {
        return requestHandler;
    }

    protected RpcInvokerFactory doGetInvokerFactory() {
        return new InterceptingRpcInvokerFactory(getDefaultInvokerFactory(), getRpcInvokeInterceptors());
    }

    protected RpcRequestFactory doGetRequestFactory() {
        return new FlameRequestFactory(getDefaultRequestFactory(), getRpcRequestInterceptors());
    }

    protected ExchangeServer doCreateServer(URL url) {
        return new DefaultExchangeServer(initServer(url, requestHandler));
    }
}
