package com.rain.flame.rpc.server;

import com.rain.flame.Channel;
import com.rain.flame.Invoker;
import com.rain.flame.rpc.RpcInvocation;

public interface RpcInvoker extends Invoker {
    RpcInvokeResult invoke(RpcInvocation invocation);
    Channel getChannel();
}
