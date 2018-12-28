package com.rain.flame.rpc;

import com.rain.flame.remoting.exchange.ExchangeRequest;
import com.rain.flame.rpc.client.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
public class InvokerInvocationHandler implements InvocationHandler {

    private final ExchangeRequest request;

    public InvokerInvocationHandler(ExchangeRequest request) {
        this.request = request;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(request, args);
        }
        if ("toString".equals(methodName) && parameterTypes.length == 0) {
            return request.toString();
        }
        if ("hashCode".equals(methodName) && parameterTypes.length == 0) {
            return request.hashCode();
        }
        if ("equals".equals(methodName) && parameterTypes.length == 1) {
            return request.equals(args[0]);
        }

        RpcInvocation invocation;
        invocation = new RpcInvocation();
        invocation.setParameterTypes(parameterTypes);
        invocation.setArguments(args);
        invocation.setMethodName(methodName);
        return request.execute(invocation).get();
    }


}
