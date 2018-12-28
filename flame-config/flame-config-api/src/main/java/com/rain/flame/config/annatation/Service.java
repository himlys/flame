package com.rain.flame.config.annatation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface Service {
    Class<?> interfaceClass() default void.class;

    String interfaceName() default "";

    String version() default "";

    boolean export() default true;

    boolean register() default true;

    String onconnect() default "";

    int timeout() default 0;

    String[] protocol() default {};

    String[] registry() default {};
}
