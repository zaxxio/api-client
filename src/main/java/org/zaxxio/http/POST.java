package com.avaand.app.httpclient.http;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface POST {
    String value() default "";
}
