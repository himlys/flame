package com.rain.flame.rpc.client;

import com.rain.flame.rpc.RpcInvocation;
import com.rain.flame.rpc.server.RpcInvokeResult;

public abstract class AbstractRpcRequestIntercetor implements RpcRequestInterceptor {
    @Override
    public RpcResponse intercept(RpcRequest request, RpcInvocation rpcInvocation, RpcRequestExecution execution) {
        RpcResponse rpcResponse = interceptBefore(request, rpcInvocation, execution);
        if(rpcResponse != null && rpcResponse.isDone()){
            return rpcResponse;
        }
        RpcResponse r = execution.execute(request, rpcInvocation);
        return r;
    }
    public RpcResponse interceptBefore(RpcRequest request, RpcInvocation rpcInvocation,RpcRequestExecution execution){
        return null;
    }

    @Override
    public RpcInvokeResult interceptAfter(RpcRequest request, RpcInvokeResult rpcInvokeResult) {
        return rpcInvokeResult;
    }
}
