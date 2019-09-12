package intercept.crypt.handler;

import intercept.crypt.annotation.CryptField;

/**
 * 加解密处理抽象类
 *
 * @author kamjin1996
 * @date 2019-07-31 11:40
 */
public interface CryptHandler<T> {

    Object encrypt(T param, CryptField cryptField);

    Object decrypt(T param, CryptField cryptField);
}
