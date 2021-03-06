package com.github.kamjin1996.mybatis.intercept.crypt.annotation;

import com.github.kamjin1996.mybatis.intercept.crypt.executor.CryptType;

import java.lang.annotation.*;

/**
 * 加解密注解
 *
 * @author kamjin1996

 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface CryptField {

    CryptType value() default CryptType.COMMON;

    boolean encrypt() default true;

    boolean decrypt() default true;
}
