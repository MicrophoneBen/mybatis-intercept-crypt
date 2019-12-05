package com.github.kamjin1996.mybatis.intercept.crypt.handler;

import com.github.kamjin1996.mybatis.intercept.crypt.annotation.CryptField;

/**
 * 加解密处理抽象类
 *
 * @author kamjin1996
 */
public interface CryptHandler<T> {

    Object encrypt(T param, CryptField cryptField);

    Object decrypt(T param, CryptField cryptField);
}
