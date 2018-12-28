package com.rain.flame.rpc.interceptors.server;

import com.rain.flame.rpc.RpcInvocation;
import com.rain.flame.rpc.server.AbstractRpcInvokeInterceptor;
import com.rain.flame.rpc.server.RpcInvokeResult;
import com.rain.flame.rpc.server.RpcInvokerExecution;

public class LoggingRpcInvokerInterceptor extends AbstractRpcInvokeInterceptor {

    @Override
    public RpcInvokeResult intercept(RpcInvocation invocation, RpcInvokerExecution execution) {
//        System.out.println("logging invoker start");
        RpcInvokeResult result = execution.execute(invocation);
//        System.out.println("logging invoker end");
        return result;
    }
}
