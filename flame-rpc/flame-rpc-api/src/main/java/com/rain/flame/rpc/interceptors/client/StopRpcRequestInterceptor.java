package com.rain.flame.rpc.interceptors.client;

import com.rain.flame.rpc.RpcInvocation;
import com.rain.flame.rpc.client.AbstractRpcRequestIntercetor;
import com.rain.flame.rpc.client.RpcRequest;
import com.rain.flame.rpc.client.RpcRequestExecution;
import com.rain.flame.rpc.client.RpcResponse;
import com.rain.flame.rpc.server.RpcInvokeResult;

public class StopRpcRequestInterceptor extends AbstractRpcRequestIntercetor {
    @Override
    public RpcResponse interceptBefore(RpcRequest request, RpcInvocation rpcInvocation, RpcRequestExecution execution) {
        RpcResponse rpcResponse = new RpcResponse() {
            @Override
            public Object get() {
                return null;
            }

            @Override
            public Object get(int timeoutInMillis) {
                return null;
            }

            @Override
            public long getId() {
                return 0;
            }

            @Override
            public boolean isDone() {
                return true;
            }
        };
        return rpcResponse;
    }

    @Override
    public RpcInvokeResult interceptAfter(RpcRequest request, RpcInvokeResult rpcInvokeResult) {
        return null;
    }
}
