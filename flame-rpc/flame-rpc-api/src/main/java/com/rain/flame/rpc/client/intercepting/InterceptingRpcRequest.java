package com.rain.flame.rpc.client.intercepting;

import com.rain.flame.common.URL;
import com.rain.flame.remoting.ChannelHandler;
import com.rain.flame.remoting.Client;
import com.rain.flame.remoting.exchange.ExchangeClient;
import com.rain.flame.rpc.Protocol;
import com.rain.flame.rpc.RpcInvocation;
import com.rain.flame.rpc.client.*;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class InterceptingRpcRequest extends AbstractRpcRequest {
    private final RpcRequestFactory requestFactory;

    private final List<RpcRequestInterceptor> rpcRequestInterceptors;
    private final RpcRequest delegate;
    private URL url;

    public InterceptingRpcRequest(RpcRequestFactory requestFactory, List<RpcRequestInterceptor> rpcRequestInterceptors, URL url, ChannelHandler handler
            , Protocol protocol) {
        super(url, handler, protocol);
        this.requestFactory = requestFactory;
        this.rpcRequestInterceptors = rpcRequestInterceptors;
        delegate = requestFactory.createRequest(getUrl(), getHandler(), rpcRequestInterceptors, protocol);
        this.url = url;
    }

    @Override
    protected Client initClient(URL url, ChannelHandler handler) {
        return null;
    }

    public boolean refreshClients(List<URL> urls) {
        boolean result = super.refreshClients(urls);
        if (result)
            delegate.setClients(getClients());
        return true;
    }

    public boolean refreshClients(ExchangeClient client, boolean add) {
        boolean result = super.refreshClients(client, add);
        if (result)
            delegate.setClients(getClients());
        return true;
    }

    @Override
    protected RpcResponse executeInternal(RpcInvocation rpcInvocation) {
        InterceptingRpcRequestExecution execution = new InterceptingRpcRequestExecution();
        return execution.execute(this, rpcInvocation);
    }


    private class InterceptingRpcRequestExecution implements RpcRequestExecution {
        private final Iterator<RpcRequestInterceptor> iterator;

        public InterceptingRpcRequestExecution() {
            this.iterator = rpcRequestInterceptors.iterator();
        }

        @Override
        public RpcResponse execute(RpcRequest rpcRequest, RpcInvocation rpcInvocation) {
            if (this.iterator.hasNext()) {
                RpcRequestInterceptor nextInterceptor = this.iterator.next();
                return nextInterceptor.intercept(rpcRequest, rpcInvocation, this);
            } else {
                return delegate.execute(rpcInvocation);
            }
        }
    }
}
