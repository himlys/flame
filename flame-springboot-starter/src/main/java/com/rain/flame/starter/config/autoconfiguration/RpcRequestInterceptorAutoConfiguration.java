package com.rain.flame.starter.config.autoconfiguration;

import com.rain.flame.rpc.interceptors.client.LoggingRpcRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RpcRequestInterceptorAutoConfiguration {
    @Bean
    public LoggingRpcRequestInterceptor loggingRpcRequestInterceptor() {
        return new LoggingRpcRequestInterceptor();
    }
}
