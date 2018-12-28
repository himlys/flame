package com.rain.flame.rpc.client.intercepting;


import com.rain.flame.rpc.client.RpcRequestFactory;

public abstract class AbstractRpcRequestFactoryWrapper implements RpcRequestFactory {
    private final RpcRequestFactory rpcRequestFactory;

    protected AbstractRpcRequestFactoryWrapper(RpcRequestFactory rpcRequestFactory) {
        this.rpcRequestFactory = rpcRequestFactory;
    }

    public RpcRequestFactory getRpcRequestFactory() {
        return rpcRequestFactory;
    }
}
