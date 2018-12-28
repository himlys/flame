package com.rain.flame.config.spring.beans.factory.annatation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import java.util.List;

import static com.rain.flame.config.spring.beans.factory.annatation.BeanReferenceUtils.getBeanNames;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;

public class RedisRegistryBeanPostProcessor implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        List<String> registrysConfigs = getBeanNames(registry, this.getClass().getClassLoader(), "com.rain.flame.config.spring.RegistryConfig");
        for (String beanName : registrysConfigs) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
            Object t = beanDefinition.getPropertyValues().getPropertyValue("type").getValue();
            if (t == null) t = "redis";
            if ("redis".equals(t)) {
                BeanDefinitionBuilder builder = rootBeanDefinition("com.rain.flame.registry.redis.RedisRegistry");
                String host = (String) beanDefinition.getPropertyValues().getPropertyValue("host").getValue();
                Object p = beanDefinition.getPropertyValues().getPropertyValue("port").getValue();
                int port = p == null ? 6379 : Integer.parseInt(p.toString());
                String redisName = createRedisTemplate(registry, beanName, host, port);
                builder.addPropertyReference("redisTemplate", redisName);
                Object n = beanDefinition.getPropertyValues().getPropertyValue("name").getValue();
                String name = n == null ? "default" : n.toString();
                registry.registerBeanDefinition(name + ".redisRegistry", builder.getBeanDefinition());
            }
        }
    }

    private String createRedisTemplate(BeanDefinitionRegistry registry, String beanName, String host, int port) {
        BeanDefinitionBuilder builder = rootBeanDefinition("org.springframework.data.redis.connection.RedisStandaloneConfiguration");
        builder.addConstructorArgValue(host);
        builder.addConstructorArgValue(port);
        registry.registerBeanDefinition(beanName + ".RedisStandaloneConfiguration", builder.getBeanDefinition());
        BeanDefinitionBuilder connBuilder = rootBeanDefinition("org.springframework.data.redis.connection.jedis.JedisConnectionFactory");
        connBuilder.addConstructorArgReference(beanName + ".RedisStandaloneConfiguration");
        registry.registerBeanDefinition(beanName + ".JedisConnectionFactory", connBuilder.getBeanDefinition());
        BeanDefinitionBuilder redisBuilder = rootBeanDefinition("org.springframework.data.redis.core.RedisTemplate");
        redisBuilder.addPropertyReference("connectionFactory", beanName + ".JedisConnectionFactory");
        registry.registerBeanDefinition(beanName + ".RedisTemplate", redisBuilder.getBeanDefinition());
        return beanName + ".RedisTemplate";
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
