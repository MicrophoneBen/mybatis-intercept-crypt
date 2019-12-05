package com.github.kamjin1996.mybatis.intercept.crypt.executor;

import com.github.kamjin1996.mybatis.intercept.crypt.util.CryptUtil;

/**
 * 普通加解密执行者
 *
 * @author kamjin1996
 */
public class CommonCryptExecutor implements CryptExecutor {

    @Override
    public String encrypt(String str) {
        return CryptUtil.encrypt(str);
    }

    @Override
    public String decrypt(String str) {
        return CryptUtil.decrypt(str);
    }
}
