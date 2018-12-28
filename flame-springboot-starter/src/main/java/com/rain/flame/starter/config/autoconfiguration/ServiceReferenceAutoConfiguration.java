package com.rain.flame.starter.config.autoconfiguration;

import com.rain.flame.common.Constants;
import com.rain.flame.common.utils.PropertyUtil;
import com.rain.flame.config.spring.ProtocolConfig;
import com.rain.flame.config.spring.RegistryConfig;
import com.rain.flame.config.spring.beans.factory.annatation.ReferenceAnnotationBeanPostProcessor;
import com.rain.flame.config.spring.context.annatation.FlameComponentScan;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;

@Configuration
@Import(FlameConfigurationRegister.class)
@EnableConfigurationProperties
public class ServiceReferenceAutoConfiguration {
    @FlameComponentScan
    class ServiceConfiguration {

    }

    @Import(ReferenceAnnotationBeanPostProcessor.class)
    class ReferenceConfiguration {

    }
}

class FlameConfigurationRegister implements ImportBeanDefinitionRegistrar, EnvironmentAware {
    private ConfigurableEnvironment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        registeRegistry(importingClassMetadata, registry);
        registeProtocol(importingClassMetadata, registry);
    }

    private void registeRegistry(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Map<String, HashMap> map = PropertyUtil.handle(environment, "flame.registrys", Map.class);
        HashMap<String, Object> reg = PropertyUtil.handle(environment, "flame.registry", HashMap.class);
        map = map == null ? new HashMap<>() : map;

        if (reg != null) map.put(Constants.DEFAULT_KEY_PREFIX, reg);
        if (map.isEmpty()) {
            HashMap<String, Object> r = new HashMap<>();
            r.put("host", "127.0.0.1");
            r.put("port", "10280");
            r.put("type", "redis");
            r.put("name", "default");
            map.put(Constants.DEFAULT_KEY_PREFIX, r);
        }
        for (String key : map.keySet()) {
            HashMap<String, Object> h = map.get(key);
            BeanDefinitionBuilder builder = rootBeanDefinition(RegistryConfig.class);
            for (String s : h.keySet()) {
                builder.addPropertyValue(s, h.get(s));
            }
            builder.addPropertyValue("name", key);
            AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
            registry.registerBeanDefinition(key + ".registry", beanDefinition);
        }
        if (!map.isEmpty() && map.keySet().size() > 0) {
            if (!registry.containsBeanDefinition(Constants.DEFAULT_KEY_PREFIX + ".registry")) {
                registry.registerAlias(map.keySet().iterator().next() + ".registry", Constants.DEFAULT_KEY_PREFIX + ".registry");
            }
        }
    }

    private void registeProtocol(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Map<String, HashMap> map = PropertyUtil.handle(environment, "flame.protocols", Map.class);
        HashMap<String, Object> reg = PropertyUtil.handle(environment, "flame.protocol", HashMap.class);
        map = map == null ? new HashMap<>() : map;

        if (reg != null) map.put(Constants.DEFAULT_KEY_PREFIX, reg);
        if (map.isEmpty()) {
            HashMap<String, Object> r = new HashMap<>();
            r.put("port", "10280");
            r.put("server", "netty");
            map.put(Constants.DEFAULT_KEY_PREFIX, r);
        }
        for (String key : map.keySet()) {
            HashMap<String, Object> h = map.get(key);
            if (!h.containsKey("host")) {
                try {
                    h.put("host", InetAddress.getLocalHost().getHostAddress());
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
            BeanDefinitionBuilder builder = rootBeanDefinition(ProtocolConfig.class);
            for (String s : h.keySet()) {
                builder.addPropertyValue(s, h.get(s));
            }
            builder.addPropertyValue("name", key);
            AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
            registry.registerBeanDefinition(key + ".protocol", beanDefinition);
            if (!registry.containsBeanDefinition(Constants.DEFAULT_KEY_PREFIX + ".protocol")) {
                registry.registerAlias(key + ".protocol", Constants.DEFAULT_KEY_PREFIX + ".protocol");
            }
        }

    }
}
