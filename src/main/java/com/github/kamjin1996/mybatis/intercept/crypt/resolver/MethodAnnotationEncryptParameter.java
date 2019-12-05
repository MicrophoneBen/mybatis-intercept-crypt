package com.github.kamjin1996.mybatis.intercept.crypt.resolver;

import com.github.kamjin1996.mybatis.intercept.crypt.annotation.CryptField;

/**
 * 方法加密注解了的参数
 *
 * @author kamjin1996
 */
class MethodAnnotationEncryptParameter {

    private String paramName;
    private CryptField cryptField;
    private Class<?> cls;

    public MethodAnnotationEncryptParameter() {
    }

    public MethodAnnotationEncryptParameter(String paramName, CryptField cryptField, Class<?> cls) {
        this.paramName = paramName;
        this.cryptField = cryptField;
        this.cls = cls;
    }


    public String getParamName() {
        return paramName;
    }

    public CryptField getCryptField() {
        return cryptField;
    }

    public Class<?> getCls() {
        return cls;
    }
}
