package com.github.kamjin1996.mybatis.intercept.crypt.resolver;

import com.github.kamjin1996.mybatis.intercept.crypt.handler.CryptHandlerFactory;

/**
 * 简单解密处理者
 *
 * @author kamjin1996
 */
public class SimpleMethodDecryptResolver implements MethodDecryptResolver {

    @Override
    public Object processDecrypt(Object param) {
        return CryptHandlerFactory.getCryptHandler(param, null).decrypt(param, null);
    }
}
