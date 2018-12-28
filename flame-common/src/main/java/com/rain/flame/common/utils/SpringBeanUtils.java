package com.rain.flame.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.beans.factory.BeanFactoryUtils.beanNamesForTypeIncludingAncestors;
import static org.springframework.beans.factory.BeanFactoryUtils.beanOfType;

public class SpringBeanUtils {
    /**
     * @throws ClassNotFoundException
     * @throws BeansException
     * @Author:LZY
     * @Date:2015/2/8
     * @Function：获取SpringBean根据类名
     * @2015/2/28
     * @desc Fixed a Exception 服务启动的时候可能还没做完context，就被调用
     */
    public static <T> T getBean(String beanName) throws BeansException {
        ApplicationContext ac = ApplicationContextHelper.getApplicationContext();
        return ac == null ? null : (T) ac.getBean(beanName);
    }

    public static <T> T getBean(ApplicationContext applicationContext, String beanName) throws BeansException {
        return applicationContext == null ? null : (T) applicationContext.getBean(beanName);
    }

    /**
     * @return
     * @Author:LZY
     * @Date:2015/2/8
     * @Function：获取SpringBean根据Class
     * @2015/2/28 Fixed a Exception 服务启动的时候可能还没做完context，就被调用
     */
    public static <T> T getBean(Class<T> clasz) {
        ApplicationContext ac = ApplicationContextHelper.getApplicationContext();

        return ac == null ? null : ac.getBean(clasz);
    }

    public static <T> List<T> getBeans(ListableBeanFactory beanFactory, String[] beanNames, Class<T> beanType) {
        String[] allBeanNames = beanNamesForTypeIncludingAncestors(beanFactory, beanType);
        List<T> beans = new ArrayList<T>(beanNames.length);
        for (String beanName : beanNames) {
            if (StringUtils.isContains(allBeanNames, beanName)) {
                beans.add(beanFactory.getBean(beanName, beanType));
            }
        }
        return Collections.unmodifiableList(beans);

    }

    public static <T> List<T> getBeans(ListableBeanFactory beanFactory, Class<T> beanType) {
        return Collections.unmodifiableList(new ArrayList<>(beanFactory.getBeansOfType(beanType).values()));
    }
}
