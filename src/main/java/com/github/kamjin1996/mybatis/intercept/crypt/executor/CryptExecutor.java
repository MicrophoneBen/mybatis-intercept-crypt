package com.github.kamjin1996.mybatis.intercept.crypt.executor;

/**
 * 加解密执行者，可能是加密手机号码，可能是加密姓名等
 *
 * @author kamjin1996
 */
public interface CryptExecutor {

    String encrypt(String str);

    String decrypt(String str);
}
