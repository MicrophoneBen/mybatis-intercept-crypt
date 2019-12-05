package com.github.kamjin1996.mybatis.intercept.crypt.executor;

/**
 * 特殊加密执行者
 *
 * @author kamjin1996
 */
public class SpecialCryptExecutor implements CryptExecutor {

    @Override
    public String encrypt(String str) {
        // TODO 特殊加密方式
        return str;
    }

    @Override
    public String decrypt(String str) {
        // TODO 特殊解密方式
        return str;
    }
}
