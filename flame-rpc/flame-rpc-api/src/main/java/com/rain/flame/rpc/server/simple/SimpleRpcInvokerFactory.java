package com.rain.flame.rpc.server.simple;

import com.rain.flame.Channel;
import com.rain.flame.common.Constants;
import com.rain.flame.common.URL;
import com.rain.flame.common.utils.SpringBeanUtils;
import com.rain.flame.common.utils.StringUtils;
import com.rain.flame.rpc.server.RpcInvoker;
import com.rain.flame.rpc.server.RpcInvokerFactory;
import org.springframework.util.ClassUtils;

public class SimpleRpcInvokerFactory<T> implements RpcInvokerFactory {
    protected T get(String service) {
        Class instanceClass = null;
        try {
            instanceClass = Class.forName(service);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String beanName = generateServiceBeanName(instanceClass);
        return SpringBeanUtils.getBean(beanName);
    }

    private String generateServiceBeanName(Class<?> interfaceClass) {
        StringBuilder beanNameBuilder = new StringBuilder();
        Class classz = ClassUtils.resolveClassName(Constants.SERVICE_BEAN_NAME, ClassUtils.getDefaultClassLoader());
        beanNameBuilder.append(classz.getSimpleName());
        beanNameBuilder.append(Constants.SEPARATOR).append(interfaceClass.getSimpleName());
        String interfaceClassName = interfaceClass.getName();
        beanNameBuilder.append(Constants.SEPARATOR).append(interfaceClassName);
        String version = "";
        if (org.springframework.util.StringUtils.hasText(version)) {
            beanNameBuilder.append(Constants.SEPARATOR).append(version);
        }
        return beanNameBuilder.toString();
    }

    @Override
    public RpcInvoker createInvoker(Channel channel, URL url) {
        String service = url.getParameter("interface");
        Object obj = get(service);
        return new SimpleRpcInvoker(channel, url, obj);
    }
}
