package com.rain.flame.config.spring.beans.factory.annatation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;

public class BeanReferenceUtils {
    public static ManagedList<RuntimeBeanReference> toRuntimeBeanReferences(BeanDefinitionRegistry registry, ClassLoader classLoader, String classz) {
        ManagedList<RuntimeBeanReference> runtimeBeanReferences = new ManagedList<RuntimeBeanReference>();
        for (String beanName : registry.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
            if (beanDefinition instanceof AbstractBeanDefinition) {
                Class rpcRequestInterceptorClass = ClassUtils.resolveClassName(classz, classLoader);
                if (((AbstractBeanDefinition) beanDefinition).hasBeanClass()) {
                    if (rpcRequestInterceptorClass.isAssignableFrom(((AbstractBeanDefinition) beanDefinition).getBeanClass())) {
                        runtimeBeanReferences.add(new RuntimeBeanReference(beanName));
                    }
                } else {
//                    估计走不到这里的
                    String beanClassName = ((AbstractBeanDefinition) beanDefinition).getBeanClassName();
                    if (beanClassName == null || "".equals(beanClassName)) continue;
                    Class beanClass = ClassUtils.resolveClassName(beanClassName, classLoader);
                    if (rpcRequestInterceptorClass.isAssignableFrom(beanClass)) {
                        runtimeBeanReferences.add(new RuntimeBeanReference(beanName));
                    }
                }
            }

        }

        return runtimeBeanReferences;
    }

    public static List<String> getBeanNames(BeanDefinitionRegistry registry, ClassLoader classLoader, String classz) {
        List<String> result = new ArrayList<>();
        for (String beanName : registry.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
            if (beanDefinition instanceof AbstractBeanDefinition) {
                Class rpcRequestInterceptorClass = ClassUtils.resolveClassName(classz, classLoader);
                if (((AbstractBeanDefinition) beanDefinition).hasBeanClass()) {
                    if (rpcRequestInterceptorClass.isAssignableFrom(((AbstractBeanDefinition) beanDefinition).getBeanClass())) {
                        result.add(beanName);
                    }
                } else {
//                    估计走不到这里的
                    String beanClassName = ((AbstractBeanDefinition) beanDefinition).getBeanClassName();
                    if (beanClassName == null || "".equals(beanClassName)) continue;
                    Class beanClass = ClassUtils.resolveClassName(beanClassName, classLoader);
                    if (rpcRequestInterceptorClass.isAssignableFrom(beanClass)) {
                        result.add(beanName);
                    }
                }
            }
        }
        return result;
    }
}
