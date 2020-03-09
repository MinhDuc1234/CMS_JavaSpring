package com.eureka.service.Validator.Setting;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.eureka.service.Enum.Action;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IPermission {
    String[] permissions();

    Action[] ignores() default {};

    String table();
}