package com.rain.flame.starter.config.autoconfiguration;

import com.rain.flame.rpc.interceptors.server.LoggingRpcInvokerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RpcInvokerInterceptorAutoConfiguration {
    @Bean
    public LoggingRpcInvokerInterceptor loggingRpcInvokerInterceptor() {
        return new LoggingRpcInvokerInterceptor();
    }
}
