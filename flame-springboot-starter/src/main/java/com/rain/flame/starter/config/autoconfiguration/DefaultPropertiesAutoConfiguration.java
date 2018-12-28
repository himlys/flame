package com.rain.flame.starter.config.autoconfiguration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:/META-INF/internal/server.properties")
public class DefaultPropertiesAutoConfiguration {

}
