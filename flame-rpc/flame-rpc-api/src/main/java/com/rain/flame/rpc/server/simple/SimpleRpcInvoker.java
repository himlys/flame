package com.rain.flame.rpc.server.simple;

import com.rain.flame.Channel;
import com.rain.flame.Invocation;
import com.rain.flame.common.URL;
import com.rain.flame.common.utils.ReflectUtils;
import com.rain.flame.rpc.RpcInvocation;
import com.rain.flame.rpc.server.AbstractRpcInvoker;
import com.rain.flame.rpc.server.RpcInvokeResult;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

public class SimpleRpcInvoker extends AbstractRpcInvoker {

    public SimpleRpcInvoker(Channel channel, URL url, Object instance) {
        super(channel, url, instance);
    }

    @Override
    public RpcInvokeResult invoke(RpcInvocation invocation) {
        if (invocation instanceof RpcInvocation) {
            Method method = ReflectUtils.findMethod(getTarget(), invocation.getMethodName(), invocation.getParameterTypes());
            Object r = ReflectUtils.invokeMethod(method, getTarget(), invocation.getArguments());
            RpcInvokeResult result = new RpcInvokeResult();
            result.setData(r);
            result.setId(invocation.getId());
            return result;
        }
        return null;
    }
}
