package com.rain.flame.rpc.server;

import com.rain.flame.Invocation;
import com.rain.flame.rpc.RpcInvocation;

public interface RpcInvokeInterceptor {
    RpcInvokeResult intercept(RpcInvocation invocation, RpcInvokerExecution execution);
}
