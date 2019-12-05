package com.github.kamjin1996.mybatis.intercept.crypt.handler;

import com.github.kamjin1996.mybatis.intercept.crypt.annotation.CryptField;

/**
 * 空的加解密执行者，避免过多空指针判断
 *
 * @author kamjin1996
 */
public class EmptyCryptHandler implements CryptHandler<Object> {

    @Override
    public Object encrypt(Object param, CryptField cryptField) {
        return param;
    }

    @Override
    public Object decrypt(Object param, CryptField cryptField) {
        return param;
    }
}
