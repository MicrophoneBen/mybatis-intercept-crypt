package com.github.kamjin1996.mybatis.intercept.crypt.resolver;

/**
 * 表示方法不需要解密
 *
 * @author kamjin1996
 */
public class EmptyMethodDecryptResolver implements MethodDecryptResolver {

    @Override
    public Object processDecrypt(Object param) {
        return param;
    }
}
