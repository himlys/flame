package com.rain.flame.rpc.client;

import com.rain.flame.Execution;
import com.rain.flame.rpc.RpcInvocation;

public interface RpcRequestExecution extends Execution {
    RpcResponse execute(RpcRequest rpcRequest, RpcInvocation rpcInvocation);
}
