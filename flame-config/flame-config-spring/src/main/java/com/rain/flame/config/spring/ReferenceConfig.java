package com.rain.flame.config.spring;

import com.rain.flame.Request;
import com.rain.flame.common.Constants;
import com.rain.flame.common.URL;
import com.rain.flame.registry.api.Registry;
import com.rain.flame.remoting.exchange.ExchangeRequest;
import com.rain.flame.remoting.exchange.client.DefaultExchangeRequest;
import com.rain.flame.rpc.InvokerInvocationHandler;
import com.rain.flame.rpc.Protocol;
import com.rain.flame.rpc.client.RpcRequest;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.rain.flame.common.utils.SpringBeanUtils.getBean;

public class ReferenceConfig<T> extends AbstractConfig implements FactoryBean, ApplicationContextAware, InitializingBean, DisposableBean {
    private transient volatile boolean initialized;
    private transient volatile boolean destroyed;
    private transient volatile T ref;
    private ApplicationContext applicationContext;

    public synchronized T get() {
        if (destroyed) {
            throw new IllegalStateException("Already destroyed!");
        }
        if (ref == null) {
            init();
        }
        return ref;
    }

    private void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        Map<String, String> map = new HashMap<String, String>();
        map.put(Constants.SIDE_KEY, Constants.CONSUMER_SIDE);
        Map<String, Request> requestMap = new HashMap<>();
        for (RegistryConfig registryConfig : registries) {
            Registry registry = getRegistry(registryConfig);
            for (ProtocolConfig protocolConfig : protocols) {
                URL url = new URL(protocolConfig.getName(), registryConfig.getHost(), registryConfig.getPort(), getPath());
                Protocol protocol = getProtocol(protocolConfig);
                String requestKey = getRequestKey(url);
                RpcRequest request = null;
                if (requestMap.containsKey(requestKey)) {
                    request = (RpcRequest) requestMap.get(requestKey);
                } else {
                    request = protocol.refer(interfaceClass, url);
                    requestMap.put(getRequestKey(url), request);
                }
                registry.subscribe(url, request);
            }
        }
        ExchangeRequest exchangeRequest = new DefaultExchangeRequest(Collections.unmodifiableList(new ArrayList<>(requestMap.values())));
        ref = (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{interfaceClass}, new InvokerInvocationHandler(exchangeRequest));
    }

    private String getRequestKey(URL url) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(Constants.URL_SPLIT).append(url.getProtocol()).append(Constants.URL_SPLIT).append(url.getPath());
        return buffer.toString();
    }

    protected Registry getRegistry(RegistryConfig config) {
        String registryName = config.getName() + ".redisRegistry";
        Registry registry = getBean(applicationContext, registryName);
        registry.setApplicationContext(applicationContext);
        return registry;
    }

    protected Protocol getProtocol(ProtocolConfig config) {
        String protocolName = config.getName() + "Protocol";
        return getBean(applicationContext, protocolName);
    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public Object getObject() throws Exception {
        return null;
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }

    @Override
    public void afterPropertiesSet() {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setInterface(Class<?> interfaceClass) {
        if (interfaceClass != null && !interfaceClass.isInterface()) {
            throw new IllegalStateException("The interface class " + interfaceClass + " is not a interface!");
        }
        this.interfaceClass = interfaceClass;
        setInterface(interfaceClass == null ? null : interfaceClass.getName());
    }

    public void setInterface(String interfaceName) {
        this.interfaceName = interfaceName;
        if (id == null || id.length() == 0) {
            id = interfaceName;
        }
    }
}
