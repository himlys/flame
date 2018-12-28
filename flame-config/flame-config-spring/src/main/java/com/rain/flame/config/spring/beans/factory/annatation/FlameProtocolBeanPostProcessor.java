package com.rain.flame.config.spring.beans.factory.annatation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import java.util.List;

import static com.rain.flame.config.spring.beans.factory.annatation.BeanReferenceUtils.toRuntimeBeanReferences;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;

public class FlameProtocolBeanPostProcessor implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        BeanDefinitionBuilder builder = rootBeanDefinition("com.rain.flame.rpc.protocol.FlameProtocol");
        List<RuntimeBeanReference> requestInterceptors = toRuntimeBeanReferences(registry, this.getClass().getClassLoader(), "com.rain.flame.rpc.client.RpcRequestInterceptor");
        if (!requestInterceptors.isEmpty()) {
            builder.addPropertyValue("requestInterceptors", requestInterceptors);
        }
        List<RuntimeBeanReference> invokeInterceptors = toRuntimeBeanReferences(registry, this.getClass().getClassLoader(), "com.rain.flame.rpc.server.RpcInvokeInterceptor");
        if (!invokeInterceptors.isEmpty()) {
            builder.addPropertyValue("invokeInterceptors", invokeInterceptors);
        }
        registry.registerBeanDefinition("flameProtocol", builder.getBeanDefinition());
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}