package com.rain.flame.rpc.client.simple;

import com.rain.flame.common.URL;
import com.rain.flame.remoting.ChannelHandler;
import com.rain.flame.remoting.Client;
import com.rain.flame.remoting.exchange.ExchangeClient;
import com.rain.flame.rpc.Protocol;
import com.rain.flame.rpc.RpcInvocation;
import com.rain.flame.rpc.client.AbstractRpcRequest;
import com.rain.flame.rpc.client.DefaultRpcResponse;
import com.rain.flame.rpc.client.RpcRequestInterceptor;
import com.rain.flame.rpc.client.RpcResponse;

import java.util.ArrayList;
import java.util.List;

public class SimpleRpcRequest extends AbstractRpcRequest {
    private List<RpcRequestInterceptor> rpcRequestInterceptors;

    public SimpleRpcRequest(URL url, ChannelHandler handler, Protocol protocol) {
        this(url, handler, new ArrayList<>(), protocol);
    }

    public SimpleRpcRequest(URL url, ChannelHandler handler, List<RpcRequestInterceptor> rpcRequestInterceptors, Protocol protocol) {
        super(url, handler, protocol);
        this.rpcRequestInterceptors = rpcRequestInterceptors;
    }

    public List<RpcRequestInterceptor> getRpcRequestInterceptors() {
        return rpcRequestInterceptors;
    }

    protected Client initClient(URL url, ChannelHandler handler) {
        return null;
    }

    protected RpcResponse executeInternal(RpcInvocation rpcInvocation) {
        ExchangeClient client = selectClient();
        while (client != null && !client.isActive()) {
            getProtocol().inactiveClient(client);
            client = selectClient();
        }
        if (client == null) throw new RuntimeException("there is no client registered");
        rpcInvocation.setUrl(client.getUrl());
        client.send(this, rpcInvocation);
        RpcResponse rpcResponse = new DefaultRpcResponse(this, 0);
        return rpcResponse;
    }
}
