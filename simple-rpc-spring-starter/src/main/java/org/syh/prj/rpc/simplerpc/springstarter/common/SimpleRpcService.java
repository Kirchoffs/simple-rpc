package org.syh.prj.rpc.simplerpc.springstarter.common;

import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface SimpleRpcService {
    int limit() default 0;
    String group() default "default";
    String serviceToken() default "default";
}
