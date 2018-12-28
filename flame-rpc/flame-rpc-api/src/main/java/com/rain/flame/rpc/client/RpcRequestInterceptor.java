package com.rain.flame.rpc.client;

import com.rain.flame.rpc.RpcInvocation;
import com.rain.flame.rpc.server.RpcInvokeResult;

public interface RpcRequestInterceptor {
    RpcResponse intercept(RpcRequest request, RpcInvocation rpcInvocation, RpcRequestExecution execution);

    RpcInvokeResult interceptAfter(RpcRequest request, RpcInvokeResult rpcInvokeResult);

    RpcResponse interceptBefore(RpcRequest request, RpcInvocation rpcInvocation, RpcRequestExecution execution);
}
