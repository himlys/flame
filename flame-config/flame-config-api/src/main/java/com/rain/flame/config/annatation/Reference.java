package com.rain.flame.config.annatation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface Reference {
    Class<?> interfaceClass() default void.class;

    String interfaceName() default "";

    String version() default "";

    String url() default "";

    String client() default "";

    String onconnect() default "";

    int timeout() default 0;

    String protocol() default "";

    String[] registry() default {};
}
