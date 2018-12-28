package com.rain.flame.config.spring.beans.factory.annatation;

import com.rain.flame.common.Constants;
import com.rain.flame.config.spring.AbstractConfig;
import com.rain.flame.config.spring.ProtocolConfig;
import com.rain.flame.config.spring.RegistryConfig;
import org.springframework.context.ApplicationContext;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import static com.rain.flame.common.utils.SpringBeanUtils.getBean;
import static com.rain.flame.common.utils.SpringBeanUtils.getBeans;
import static org.springframework.beans.factory.BeanFactoryUtils.beanNamesForTypeIncludingAncestors;

abstract class AbstractAnnotationConfigBeanBuilder<A extends Annotation, B extends AbstractConfig> {

    protected final A annotation;

    protected final ApplicationContext applicationContext;

    protected final ClassLoader classLoader;

    protected Object bean;

    protected Class<?> interfaceClass;

    AbstractAnnotationConfigBeanBuilder(A annotation, ApplicationContext applicationContext, ClassLoader classLoader) {
        this.annotation = annotation;
        this.applicationContext = applicationContext;
        this.classLoader = classLoader;
    }

    public final B build() throws Exception {
        checkDependencies();
        B bean = doBuild();
        configureBean(bean);
        return bean;
    }

    protected abstract B doBuild();

    protected void configureBean(B bean) {
        preConfigureBean(annotation, bean);
        configureRegistryConfigs(bean);
        configureProtocolConfigs(bean);
//        configureMonitorConfig(bean);
//        configureApplicationConfig(bean);
//        configureModuleConfig(bean);
        postConfigureBean(annotation, bean);
    }

    private void configureRegistryConfigs(B bean) {
        String[] registryConfigBeanIds = resolveRegistryConfigBeanNames(annotation);
        List<RegistryConfig> registryConfigs = getBeans(applicationContext, registryConfigBeanIds, RegistryConfig.class);
        if (registryConfigs.isEmpty()) {
            registryConfigs = new ArrayList<>();
            registryConfigs.addAll(getBeans(applicationContext, RegistryConfig.class));
        }
        bean.setRegistries(registryConfigs);
    }

    private void configureProtocolConfigs(B bean) {
        String[] protocolConfigBeanIds = resolveRegistryConfigBeanNames(annotation);
        List<ProtocolConfig> protocolConfigs = getBeans(applicationContext, protocolConfigBeanIds, ProtocolConfig.class);
        if (protocolConfigs.isEmpty()) {
            protocolConfigs = new ArrayList<>();
            protocolConfigs.add(getBean(applicationContext, Constants.DEFAULT_KEY_PREFIX + Constants.COMMA + "protocol"));
        }
        bean.setProtocols(protocolConfigs);
    }

    protected abstract String[] resolveRegistryConfigBeanNames(A annotation);

    protected abstract void preConfigureBean(A annotation, B bean);

    protected abstract void postConfigureBean(A annotation, B bean);

    private void checkDependencies() {

    }

    public <T extends AbstractAnnotationConfigBeanBuilder<A, B>> T interfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
        return (T) this;
    }
}
