package com.rain.flame.config.spring;

import com.rain.flame.common.URL;
import com.rain.flame.config.annatation.Service;
import org.springframework.core.env.Environment;

import java.util.List;

public abstract class ServiceConfig<T> extends AbstractServiceConfig{
    private static final long serialVersionUID= -3577163788320615869L;
    private T ref;
    public ServiceConfig() {
    }

    public T getRef() {
        return ref;
    }

    public void setRef(T ref) {
        this.ref = ref;
    }

    public ServiceConfig(Service service) {
//        appendAnnotation(Service.class, service);
    }
    public void setEnvironment(Environment environment) {
        super.setEnvironment(environment);
    }
}
