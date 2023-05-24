package com.avaand.app.httpclient.http;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Query {
    String value() default "";
}
