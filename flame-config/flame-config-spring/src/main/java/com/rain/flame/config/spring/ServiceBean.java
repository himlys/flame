package com.rain.flame.config.spring;

import com.rain.flame.common.URL;
import com.rain.flame.common.utils.SpringBeanUtils;
import com.rain.flame.config.annatation.Service;
import com.rain.flame.registry.api.Registry;
import com.rain.flame.rpc.Protocol;
import com.rain.flame.rpc.client.RpcRequest;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import static com.rain.flame.common.utils.SpringBeanUtils.getBean;

public class ServiceBean<T> extends ServiceConfig<T> implements InitializingBean, DisposableBean, ApplicationContextAware, ApplicationListener<ContextRefreshedEvent>, BeanNameAware {
    private static final long serialVersionUID = -8895958544046081993L;
    private final transient Service service;
    private transient ApplicationContext applicationContext;
    private transient volatile boolean exported;

    private transient volatile boolean unexported;

    public ServiceBean() {
        super();
        this.service = null;
    }

    public ServiceBean(Service service) {
        this.service = service;
    }

    @Override
    public void setBeanName(String name) {

    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        super.setEnvironment(this.applicationContext.getEnvironment());
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!isExported() && !isUnexported()) {
            doExport();
        }
    }

    protected synchronized void doExport() {
        if (unexported) {
            throw new IllegalStateException("Already unexported!");
        }
        if (exported) {
            return;
        }
        exported = true;
//        if (interfaceName == null || interfaceName.length() == 0) {
//            throw new IllegalStateException("<dubbo:service interface=\"\" /> interface not allow null!");
//        }
        doExportUrls();
    }

    private void doExportUrls() {
        for (ProtocolConfig protocolConfig : protocols) {
            doExportUrlsFor1Protocol(protocolConfig);
        }
    }

    protected Protocol getProtocol(ProtocolConfig config) {
        String protocolName = config.getName() + "Protocol";
        return getBean(protocolName);
    }

    private void doExportUrlsFor1Protocol(ProtocolConfig config) {
        URL url = getURL(config);
        Protocol protocol = getProtocol(config);
        protocol.export(url);
        for (RegistryConfig registryConfig : registries) {
            Registry registry = getRegistry(registryConfig);
            registry.register(url);
        }
    }

    private Registry getRegistry(RegistryConfig config) {
        String registryName = config.getName() + ".redisRegistry";
        Registry registry = getBean(applicationContext, registryName);
        registry.setApplicationContext(applicationContext);
        return registry;
    }

    public boolean isExported() {
        return exported;
    }

    public boolean isUnexported() {
        return unexported;
    }
}
