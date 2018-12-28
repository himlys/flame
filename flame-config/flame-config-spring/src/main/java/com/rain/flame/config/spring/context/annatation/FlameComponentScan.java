package com.rain.flame.config.spring.context.annatation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(FlameComponentScanRegistrar.class)
public @interface FlameComponentScan {
    String[] value() default {"com"};
    String[] basePackages() default {};
    Class<?>[] basePackageClasses() default {};
}
