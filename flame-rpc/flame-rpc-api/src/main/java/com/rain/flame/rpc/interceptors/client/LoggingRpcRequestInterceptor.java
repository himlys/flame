package com.rain.flame.rpc.interceptors.client;

import com.rain.flame.rpc.RpcInvocation;
import com.rain.flame.rpc.client.AbstractRpcRequestIntercetor;
import com.rain.flame.rpc.client.RpcRequest;
import com.rain.flame.rpc.client.RpcRequestExecution;
import com.rain.flame.rpc.client.RpcResponse;
import com.rain.flame.rpc.server.RpcInvokeResult;

public class LoggingRpcRequestInterceptor extends AbstractRpcRequestIntercetor {
    public RpcResponse interceptBefore(RpcRequest request, RpcInvocation rpcInvocation, RpcRequestExecution execution) {
//        System.out.println("logging start " + rpcInvocation);
        return null;
    }

    @Override
    public RpcInvokeResult interceptAfter(RpcRequest request, RpcInvokeResult rpcInvokeResult) {
//        System.out.println("logging end");
        return rpcInvokeResult;
    }

}
