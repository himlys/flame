package com.rain.flame.config.spring;

import org.springframework.core.env.Environment;

public abstract class AbstractServiceConfig extends AbstractConfig{
    private static final long serialVersionUID= -4371330916863655557L;

    public void setEnvironment(Environment environment) {
        super.setEnvironment(environment);
    }
}
