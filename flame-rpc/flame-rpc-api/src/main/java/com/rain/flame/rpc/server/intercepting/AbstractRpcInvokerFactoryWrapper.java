package com.rain.flame.rpc.server.intercepting;

import com.rain.flame.common.utils.SpringBeanUtils;
import com.rain.flame.common.utils.StringUtils;
import com.rain.flame.rpc.server.RpcInvokerFactory;

public abstract class AbstractRpcInvokerFactoryWrapper<T> implements RpcInvokerFactory {
    private final RpcInvokerFactory rpcInvokerFactory;

    protected AbstractRpcInvokerFactoryWrapper(RpcInvokerFactory rpcInvokerFactory) {
        this.rpcInvokerFactory = rpcInvokerFactory;
    }

    public RpcInvokerFactory getRpcInvokerFactory() {
        return rpcInvokerFactory;
    }
    protected T get(String service) {
        Class instanceClass = null;
        try {
            instanceClass = Class.forName(service);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return SpringBeanUtils.getBean(StringUtils.upperFirstChar(instanceClass.getSimpleName()));
    }
}
