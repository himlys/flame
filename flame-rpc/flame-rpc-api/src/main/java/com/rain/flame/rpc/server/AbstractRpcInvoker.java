package com.rain.flame.rpc.server;

import com.rain.flame.Channel;
import com.rain.flame.Invocation;
import com.rain.flame.common.URL;
import com.rain.flame.remoting.exchange.ExchangeInvocationResult;
import com.rain.flame.rpc.RpcInvocation;

public abstract class AbstractRpcInvoker<T> implements RpcInvoker {
    private final URL url;
    private final Object target;
    private final String methodName;
    private final Channel channel;

    public String getMethodName() {
        return methodName;
    }

    public AbstractRpcInvoker(Channel channel, URL url, Object target) {
        this.url = url;
        this.channel = channel;
        this.target = target;
        this.methodName = url.getParameter("method");
    }

    public Object getTarget() {
        return target;
    }

    public URL getUrl() {
        return url;
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public Object invoke(Object message) {
        if (message instanceof RpcInvocation) {
            RpcInvokeResult result = invoke((RpcInvocation) message);
            ExchangeInvocationResult response = new ExchangeInvocationResult();
            response.setData(result);
            response.setId(result.getId());
            response.setResponseCode(ExchangeInvocationResult.ResponseCode.OK);
            return response;
        }
        return null;
    }
}
