package com.github.kamjin1996.mybatis.intercept.crypt.resolver;

import com.github.kamjin1996.mybatis.intercept.crypt.handler.CryptHandlerFactory;

/**
 * 简单加密处理者
 *
 * @author kamjin1996
 */
public class SimpleMethodEncryptResolver implements MethodEncryptResolver {

    private MethodAnnotationEncryptParameter encryptParameter;

    public SimpleMethodEncryptResolver() {
    }

    public SimpleMethodEncryptResolver(MethodAnnotationEncryptParameter encryptParameter) {
        this.encryptParameter = encryptParameter;
    }

    @Override
    public Object processEncrypt(Object param) {
        return CryptHandlerFactory.getCryptHandler(param, encryptParameter.getCryptField()).encrypt(param,
                encryptParameter.getCryptField());
    }
}
