package com.rain.flame.starter.config.autoconfiguration;

import com.rain.flame.config.spring.beans.factory.annatation.FlameProtocolBeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
public class PrototolAutoConfiguration {
    @Configuration
    @Import(FlameProtocolBeanPostProcessor.class)
    class FlameProtocolAutoConfiguration {

    }
}

