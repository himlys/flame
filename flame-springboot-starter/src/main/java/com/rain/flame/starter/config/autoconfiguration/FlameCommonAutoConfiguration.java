package com.rain.flame.starter.config.autoconfiguration;

import com.rain.flame.common.utils.ApplicationContextHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ApplicationContextHelper.class)
public class FlameCommonAutoConfiguration {

}
