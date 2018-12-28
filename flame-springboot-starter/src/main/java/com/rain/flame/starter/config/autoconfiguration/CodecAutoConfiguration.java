package com.rain.flame.starter.config.autoconfiguration;

import com.rain.flame.rpc.protocol.FlameCodec;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CodecAutoConfiguration {
    @Bean
    @ConditionalOnClass(FlameCodec.class)
    public FlameCodec flameCodec(){
        return new FlameCodec();
    }
}
