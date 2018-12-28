package com.rain.flame.starter.config.autoconfiguration;

import com.rain.flame.serialization.fst.FstSerialization;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SerializationAutoConfiguration {
    @Bean("fstSerialization")
    @ConditionalOnClass(FstSerialization.class)
    public FstSerialization fstSerialization() {
        return new FstSerialization();
    }
}