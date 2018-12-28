package com.rain.flame.rpc.server;

import com.rain.flame.Invocation;
import com.rain.flame.rpc.RpcInvocation;

public interface RpcInvokerExecution {
    public RpcInvokeResult execute(RpcInvocation invocation);
}
